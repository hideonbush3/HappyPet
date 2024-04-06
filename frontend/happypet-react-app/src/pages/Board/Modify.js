import { useEffect, useState } from 'react';
import style from './Writing.module.css';
import { useLocation, useNavigate } from 'react-router-dom';
import { API_BASE_URL } from '../../api-config';
export default function Modify(){
    const location = useLocation();
    const post = location.state.post;
    const navigate = useNavigate();
    const [urlAndFile, setUrlAndFile] = useState(new Map());

    const [title, setTitle] = useState(post.title);
    const [content, setContent] = useState(post.content);
  
    // 첫 렌더링시 원래내용 표시
    useEffect(() => {
        const contentContainer = document.getElementById('content');
        contentContainer.innerHTML = post.content;
    }, []);

    const cancelEventHandler = () => {
        navigate('/board/view', {state: {post: post}});
    };

    const handleTitleChange = (e) => {
      setTitle(e.target.value);
    };
  
    const handleContentChange = (e) => {
        const content = document.getElementById('content');
        setContent(content.innerHTML);
    };
  
    // 본문 입력마다 내용을 추적했을때 첨부했던 이미지를 삭제했는지 파악하고
    // 삭제했을경우 요청할때 보낼 데이터를 업데이트하기 위함
    useEffect(() => {
        detachImage();
    }, [content])
    
    const detachImage = () => {
        const imgs = document.getElementById('content').querySelectorAll('img');
        
        if(imgs.length === 0) return;
        
        const newMap = new Map();
        imgs.forEach((img) => {
            if(urlAndFile.has(img.src)){
                newMap.set(img.src, urlAndFile.get(img.src));
            }
        });
        
        setUrlAndFile(newMap);
    }

    const focusContentInput = () => {
        document.getElementById('content').focus({preventScroll: true});
    };

    const handleImageUpload = (e) => {
        const files = e.target.files;
        const originalUrlAndFile = new Map(urlAndFile);

        if(!!files){
            for(const file of files){
                focusContentInput();

                const img = document.createElement('img');
                img.src = URL.createObjectURL(file);
                document.execCommand('insertHTML', false, `<br>${img.outerHTML}<br>`);

                originalUrlAndFile.set(img.src, file);
            }
        }
        setUrlAndFile(originalUrlAndFile);
        e.target.value = null;
    }

    const handleSubmit = (e) => {
        e.preventDefault();
        if(post.title === title && post.content === content){
            alert("변경된 내용이 없습니다.");
            return;
        }else if(title.trim() === ''){
            alert('제목을 입력하세요.');
            return;
        }

        const input = document.getElementById('content');
        const text = input.textContent.trim();
        const imgs = input.querySelectorAll('img');

        if(text === '' && imgs.length === 0){
            alert('내용을 입력하세요');
            return;
        }

        const formData = new FormData();
        formData.append('postId', post.id);
        formData.append('title', title);
        formData.append('content', content);
        
        // 본문 입력창에 이미지가 하나 이상 존재하고, 원본 게시글이 이미지를 하나 이상 갖고있을 경우
        // 원본의 이미지들과 본문 이미지들을 비교해서 원본엔 있지만 본문에 없을 경우(작성자가 원본을 삭제)
        // 서버에 삭제됐음을 알리기위해 imagesToDelete 배열에 해당 이미지들의 이미지명을 담는다.
        let imagesToDelete = [];
        const originImgs = post.imageList;
        let originImgNames = [];
        let originBytesSize = 0;

        if(originImgs.length > 0){
            originImgs.forEach(image => {
                originImgNames.push(image.name);
                originBytesSize += image.bytes;
            })
        }
        
        if(imgs.length > 0 && originImgNames.length > 0){
            let allImagesNames = [];
            imgs.forEach((image) => {
                const url = new URL(image.src);
                const originName = decodeURIComponent(url.pathname.split('/').pop());
                allImagesNames.push(originName);
            })
            imagesToDelete = originImgNames.filter(
                (imageName) => !allImagesNames.includes(imageName));
        }else if(imgs.length === 0 && originImgNames.length > 0){
            imagesToDelete = originImgNames;
        }
        formData.append('imagesToDelete', imagesToDelete);

        if(imagesToDelete.length > 0){
            imagesToDelete.forEach(imgName => {
                originImgs.forEach(img => {
                    if(img.name === imgName){
                        originBytesSize -= img.bytes;
                    }
                })
            })
        }

        let newImgsByteSize = 0;
        if(urlAndFile.size > 0){
            const files = Array.from(urlAndFile.values());
            files.forEach((file) => {
                newImgsByteSize += file.size;
                formData.append('images', file);
            });
            const allImgsKbyteSize = (newImgsByteSize + originBytesSize) / 1024;
            if(allImgsKbyteSize > 20){
                alert(`첨부한 이미지 파일들의 총 용량이 20KB를 넘으면 안됩니다.\n현재 첨부한 이미지 파일의 총 크기: ${allImgsKbyteSize.toString().substring(0, 5)}KB`);
                return;
            }

            const urlAndName = new Map();
            urlAndFile.forEach((value, key) => {
                urlAndName.set(key, value.name);
            })

            const result = JSON.stringify(Object.fromEntries(urlAndName));
            formData.append('urlAndName', result);
        }
         
        const options = {
            url: API_BASE_URL + '/post/modify',
            method: 'PUT',
            body: formData,
            headers: {
                "Authorization": "Bearer " + localStorage.getItem('happypetToken'),
            }
        };

        return fetch(options.url, options)
        .then((res) => {
            if(res.status === 200) {
                return res.json();
            }else if(res.status === 403){
                window.location.href = "/user/login";
            }else if(res.status === 415){
                alert('이미지 파일만 첨부 가능합니다.');
                return;
            }else{
                alert('알 수 없는 에러가 발생했습니다.\n관리자에게 문의하세요.');
                return;
            }
        })
        .then((res) => {
            if(res.error !== null){
                alert('알 수 없는 에러가 발생했습니다.\n관리자에게 문의하세요.');
                return;
            }
            else{
                navigate('/board/view', {state: {post: res.object}});
            }
          })
    };
  
    return(
        <div className={style.body}>
        <div className={style.container}>
            <h2>게시글 수정</h2>
            
            <div className={style.postForm}>
                <div className={style.title}>
                    <input 
                        placeholder='제목을 입력하세요'
                        value={title}
                        onChange={handleTitleChange}/>
                </div>
                <div id='content'
                    contentEditable='true'
                    className={style.content}
                    onInput={handleContentChange}>
                </div>
            </div>
            <div className={style.btn}>
                    <button onClick={cancelEventHandler}>취소</button>
                    <button onClick={handleSubmit}>수정 완료</button>               
            </div>
            <input multiple="multiple" type='file' id='imageInput' accept='image/*' onChange={handleImageUpload}/>
        </div>
        </div>
    )
}