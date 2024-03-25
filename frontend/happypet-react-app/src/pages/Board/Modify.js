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
    }, [])  

    const cancelEventHandler = () => {
        navigate('/board/view', {state: {post: post}});
    }

    const handleTitleChange = (e) => {
      setTitle(e.target.value);
    };
  
    const handleContentChange = (e) => {
        const content = document.getElementById('content');
        setContent(content.innerHTML);
    };
  
    const focusContentInput = () => {
        document.getElementById('content').focus({preventScroll: true});
    }
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
            alert("변경된 내용이 없습니다");
            return;
        }else if(title === ''){
            alert('제목을 입력하세요');
            return;
        }

        const input = document.getElementById('content');
        const text = input.textContent.trim();
        const images = input.querySelectorAll('img');

        if(text === '' && images.length ===0){
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
        const originImages = post.imageList;
        let originImagesNames = [];

        if(originImages.length > 0){
            originImages.forEach(image => {
                originImagesNames.push(image.name);
            })
        }
        
        if(images.length > 0 && originImagesNames.length > 0){
            let allImagesNames = [];
            images.forEach((image) => {
                const url = new URL(image.src);
                const originName = decodeURIComponent(url.pathname.split('/').pop());
                allImagesNames.push(originName);
            })
            imagesToDelete = originImagesNames.filter(
                (imageName) => !allImagesNames.includes(imageName));
        }else if(images.length === 0 && originImagesNames.length > 0){
            imagesToDelete = originImagesNames;
        }
        formData.append('imagesToDelete', imagesToDelete);

        if(urlAndFile.size > 0){
            const files = Array.from(urlAndFile.values());
            files.forEach((file) => {
                formData.append('images', file);
            });

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
                "Authorization": "Bearer " + localStorage.getItem('token'),
            }
        };

        return fetch(options.url, options).then((res) => {
            if(res.status === 200) {
                return res.json();
            }else if(res.status === 403){
                window.location.href = "/user/login";
            }else{
                return res.json();
            }
        })
        .then((res) => {
            if(res.error === undefined){
                navigate('/board/view', {state: {post: res}});
            }else if(res.error === '존재하지 않는 유저'){
                alert('세션이 만료됐습니다.\n재로그인하세요.');
                localStorage.setItem('token', null);
                window.location.href = '/user/login';
            }else{
                alert('페이지를 새로고침한 후 재작성하세요.\n그래도 작성이 안된다면 관리자에게 문의하세요.')
            }
          })
        .catch(error => {
            throw error;
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