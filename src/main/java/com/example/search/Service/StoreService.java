//11. 데이터베이스 작업처리
package com.example.search.Service;

import com.example.search.DTO.StoreDTO;
import com.example.search.Entity.StoreEntity;
import com.example.search.Repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.PublicKey;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class StoreService {
    private final StoreRepository storeRepository;
    private final ModelMapper modelMapper;

    //삽입, 수정, 삭제, 개별조회는 변동이 없다.
    //조회는 일반조회, 페이징처리, 검색에 따라서 변동이 심하다.
    /*---------------------------------------------
    함수명 : StoreDelete(Integer idx)
    인  수 : 일련번호
    출  력 : 성공하면 true, 실패하면 false
    내  용 : 입력받은 idx에 해당하는 데이터를 삭제 후
             성공하면 true, 실패하면 false
     ----------------------------------------------*/
    public Boolean storeDelete(Integer idx) {
        storeRepository.deleteById(idx);

        //삭제 후 해당 레코드가 존재하는지 확인
        Optional<StoreEntity> storeEntity = storeRepository.findById(idx);
        if (storeEntity.isPresent()) {
            return false; //삭제 실패
        }
        return true; //삭제 성공
    }

    /*---------------------------------------------
    함수명 : StoreInsert(StoreDTO storeDTO)
    인  수 : 입력폼으로부터 입력받은 데이터(레코드)
    출  력 : 성공시 StoreDTO, 실패시 null
    내  용 : 입력받은 데이터를 데이터베이스에 저장
     ----------------------------------------------*/
    public StoreDTO storeInsert(StoreDTO storeDTO) {
        //DTO를 Entity로 변환 -> Repository 저장
        StoreEntity storeEntity = modelMapper.map(storeDTO, StoreEntity.class);

        StoreEntity result = storeRepository.save(storeEntity);
        //Entity 결과를 전달할 때는
        //return storeRepository.save(storeEntity);
        return modelMapper.map(result, StoreDTO.class);
    }

    /*---------------------------------------------
    함수명 : storeUpdate(StoreDTO storeDTO)
    인  수 : 수정할 storeDTO
    출  력 : 수정한 storeDTO
    설  명 : 수정폼에서 수정한 데이터를 저장하고
            저장한 DTO를 반환
     ----------------------------------------------*/
    public StoreDTO storeUpdate(StoreDTO storeDTO) {
        //DTO를 이용해서 존재여부 확인 -> DTO를 Entity 변환 -> 저장 -> 결과를 DTO 변환 후 반환
        Optional<StoreEntity> read = storeRepository.findById(storeDTO.getIdx());

        if (read.isPresent()) {//수정할 데이터가 존재하면
            //Entity 변환
            StoreEntity storeEntity = modelMapper.map(storeDTO, StoreEntity.class);
            //저장
            StoreEntity result = storeRepository.save(storeEntity);
            //저장한 결과를 변환해서 반환
            return modelMapper.map(result, StoreDTO.class);
        }
        //수정할 데이터가 존재하지 않으면
        return null;
    }

    /*---------------------------------------------
    함수명 : storeRead(Integer idx)
    인  수 : 조회할 일련번호
    결  과 : 조회한 결과 DTO
    설  명 : 지정된 번호로 데이터베이스에서 조회하여 결과를 반환
     ----------------------------------------------*/
    public StoreDTO storeRead(Integer idx) {
        //idx 조회 -> Entity를 DTO로 변환 -> 결과를 전달
        Optional<StoreEntity> read = storeRepository.findById(idx);

        if (read.isPresent()) { //조회한 결과가 있으면
            StoreDTO storeDTO = modelMapper.map(read, StoreDTO.class);
            return storeDTO;
        }
        //조회한 내용이 없으면
        return null;
    }

    /*---------------------------------------------
   함수명 : storeList(Pageable pageable, String type, String keyword)
   인  수 : 조회할 페이지 번호, 검색대상(분류), 검색어
   결  과 : 조회한 페이지정보가 있는 DTO
   설  명 : 키워드를 해당 분류에서 조회를 해서 해당페이지의 데이터를 읽어서 반환
     ----------------------------------------------*/
    public Page<StoreDTO> storeList(Pageable pageable, String type, String keyword) {
        //type       : 총판명, 총판ID, 총판장ID, 총판장, 총판명+총판ID, 총판장ID+총판명, 전체
        //숫자(영문자) :   1     2        3       4         5             6         0

        //페이지 정보 변경 => type으로 조회구분 =>키워드로 해당 조회 => entity를 DTO 변환 => 결과를 전달
        int currentPage = pageable.getPageNumber() - 1;
        int pageLimit = 10;
        Pageable storePage = PageRequest.of(currentPage, pageLimit, Sort.by(Sort.Direction.ASC, "idx")); //정렬(일련번호로 오름차순)

        //블럭안에서 변수를 선언하면 블럭안에서만 유지가 되고 블럭을 벗어나면 변수는 소멸
        //변수선언
        Page<StoreEntity> storeEntities;    //조회한 변수를 선언(****)

        if (keyword != null) { //검색어가 존재하면
            if (type.equals("1")) { // type 분류가 1이면, 총판명으로 검색할 때
                storeEntities = storeRepository.findByStoreNameContaining(keyword, storePage);
                //storeEntities = storeRepository.searchStoreName(keyword, storePage);

            } else if (type.equals("2")) { //총판ID로 검색할 때
                storeEntities = storeRepository.findByStoreIdContaining(keyword, storePage);
                //storeEntities = storeRepository.searchStoreId(keyword, storePage);
            } else if (type.equals("3")) { //총판장ID로 검색할 때
                storeEntities = storeRepository.findByStoreChiefIdContaining(keyword, storePage);
                //storeEntities = storeRepository.searchStoreChiefId(keyword, storePage);

            } else if (type.equals("4")) { //총판장명으로 검색할 때
                storeEntities = storeRepository.findByStoreChiefContaining(keyword, storePage);
                //storeEntities = storeRepository.searchStoreChief(keyword, storePage);

            } else if (type.equals("5")) { //총판명+총판ID로 검색할 때
                //storeEntities = storeRepository.findByStoreNameContainingOrStoreIdContaining(keyword, keyword, storePage);
                storeEntities = storeRepository.searchStoreAll(keyword, storePage);

            } else if (type.equals("6")) { //총판장ID +총판장으로 검색할 때
                //storeEntities = storeRepository.findByStoreChiefIdContainingOrStoreChiefContaining(keyword, keyword, storePage);
                storeEntities = storeRepository.searchStoreChiefAll(keyword, storePage);

            } else { // 모든 대상으로 검색할 때
                //storeEntities = storeRepository.findByStoreNameContainingOrStoreIdContainingOrStoreChiefIdContainingOrStoreChiefContaining(

                //           keyword, keyword, keyword, keyword storePage
                // );

                storeEntities = storeRepository.searchAll(keyword, storePage);
            }
        } else { //검색어가 존재하지 않으면 모두 검색
            storeEntities = storeRepository.findAll(storePage);
        }
        //변환 후 전달
        Page<StoreDTO> storeDTOS = storeEntities.map(entity -> modelMapper.map(entity, StoreDTO.class));

        return storeDTOS;

    }
}
/*
switch(type) {
case 1:
    break;
case 2:
    break;
case 3:
    break;
case 4:
    break;
case 5:
    break;
case 6:
    break;
defalt
 */



    /*---------------------------------------------

     ----------------------------------------------*/
