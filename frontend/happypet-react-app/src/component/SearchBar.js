import { useState } from "react";
import { FaSearch } from "react-icons/fa";
import style from './SearchBar.module.css';

function SearchBar(props){
  const [selectedSigun, setSelectedSigun] = useState("자치시");
  const [selectedType, setSelectedType] = useState("시설유형");

  function changeSigunEventHandler(e){
    e.preventDefault()
    if(e.target.value !== '자치시'){
      setSelectedSigun(e.target.value);
      let types = props.facilityList
        .filter((facility) => facility.sigun === e.target.value)
        .map((facility) => facility.type)
      let uniqueTypes = [...new Set(types)];
      props.setTypes(uniqueTypes.sort());
    }else{
      setSelectedSigun(e.target.value);
      let types = props.facilityList
        .map((facility) => facility.type)
      let uniqueTypes = [...new Set(types)];
      props.setTypes(uniqueTypes.sort());
    }
    setSelectedType('시설유형')
  }

  function changeTypeEventHandler(e){
    e.preventDefault();
      if(e.target.value!=="시설유형"){
          setSelectedType(e.target.value);
      }else setSelectedType('시설유형');
  }

  const searchTermChangeHandler = (e) => {
    e.preventDefault();
    props.setSearchTerm(e.target.value);
  }

  return(
      <div className={style.container}>

          <div>
            <select onChange={changeSigunEventHandler}>
              <option>자치시</option>
              {props.siguns !== undefined && props.siguns.map((sigun, index) => (
                <option value={sigun} key={index}>{sigun}</option>
                ))}
            </select>
          </div>
          <div>
            <select onChange={changeTypeEventHandler} value={selectedType}>
              <option>시설유형</option>
              {props.types !== undefined && props.types.map((type, index) => (
                  <option value={type} key={index}>{type}</option>
                ))}
              </select>
          </div>

          <div className={`${style.input_container}`}>
            <div className={`${style.input_group}`}>
              <input placeholder="시설명을 입력하세요" value={props.searchTerm} onChange={searchTermChangeHandler}/>
              <button onClick={() => props.searchFacilityBtnEventHandler(selectedSigun, selectedType, props.searchTerm)}>
                <FaSearch color='#232323'/>
              </button>
            </div>
          </div>


    </div>
  )
}

export default SearchBar;