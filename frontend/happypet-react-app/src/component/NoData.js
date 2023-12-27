import style from './NoData.module.css';
export default function NoData(props){
    return(
        <div className={style.no_data}>
            <p>{props.message}</p>
        </div>
    )
}