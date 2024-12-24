//12. 매핑 및 결과 메세지 처리
package com.example.search.Controller;

import com.example.search.DTO.StoreDTO;
import com.example.search.Service.StoreService;
import com.example.search.Util.PagenationUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@Log //확인이 필요한 부분에 기재해서, 동작을 확인(준비, 입력, 처리, 출력)
@Tag(name="작업클래스명", description="클래스에 대한 설명")
public class StoreController {
    private final StoreService storeService;
    private final PagenationUtil pagenationUtil;

    //서비스에 메소드를 보면서 작성
    /*------------------------------------------
    함수명 : storeDeleteProc(Integer idx)
    입  력 : html로부터 삭제할 일련번호
    출  력 : 성공/실패 메세지
    내  용 : 해당번호로 데이터를 삭제후 메세지를 가지고 list페이지로 이동
    다른 맵핑으로 값과 함께 이동할 때는 addAttributes를 사용
    ----------------------------------------- */
   @Operation(summary="작업명", description = "작업설명")
   @GetMapping("/storeDelete")
    public String storeDeleteProc(Integer idx, RedirectAttributes redirectAttributes){
       log.info(idx+"로 요청");
        Boolean result = storeService.storeDelete(idx);

       log.info("삭제처리 후 검증");
        //결과처리
        if(result) {
            //삭제하였습니다.
            redirectAttributes.addFlashAttribute("message", "삭제하였습니다.");

        } else {
            //삭제를 실패하였습니다.
            redirectAttributes.addFlashAttribute("message", "삭제를 실패하였습니다.");

        }

       log.info("목록맵핑으로 요청");
        return "redirect:/storeList";

    }
    /*------------------------------------------
    함수명 : storeInsertForm()
    인  수 : 없음
    출  력 : 삽입폼으로 이동
    설  명 : 해당맵핑의 요청이 있으면 해당 HTML로 이동
    ----------------------------------------- */
    @Operation(summary = "총판 등록폼", description = "총판 등록폼으로 이동(insert.html)")
    @GetMapping("/storeInsert")
    public String storeInsertForm(Model model){
        //검증 라이브러리 추가하면
        //입력폼에서 object, field를 이용해서 검증처리
        log.info("빈DTO를 생성후 저장");

        //입력 : 검증시 입력한 값이나 오류메시지 출력을 위해서 빈 DTO를 폼에 전달
        model.addAttribute("storeDTO", new StoreDTO());
        //getMapping model.addAttribute("storeDTO") 이름과
        //postMapping (StoreDTO storeDTO)이름은 동일하게 지정

        log.info("입력폼으로 이동");
        return "insert";
    }
    /*------------------------------------------
    함수명 : storeInsertProc(StoreDTO storeDTO)
    인  수 : 입력한 StoreDTO
    출  력 : 저장 결과 메세지를 가지고 list로 이동
    설  명 : 입력받은 데이터를 데이터베이스에 저장하고, 결과를 가지고 List맵핑으로 이동

    ----------------------------------------- */
    @Operation(summary = "총판 등록처리", description = "총판 등록폼의 정보를 데이터베이스에 저장한다.")
    @PostMapping("/storeInsert")
    //검증이 필요한 DTO에 @Valid 선언과 함께 BindingResult를 선언
    //적용 순서는 반드시 지켜서 기재(BindingResult가 RedirectAttributes보다 먼저 나와야함)
    public String storeInsertProc(@Valid @ModelAttribute StoreDTO storeDTO,
                                  BindingResult bindingResult,
                                  RedirectAttributes redirectAttributes
                                  ) {

        //HTML -> DTO검증 -> 서비스에서 데이터베이스 처리 -> 결과 -> 페이지이동
        //검증의 유효성판단은 서비스 전에 작업해야함
        if(bindingResult.hasErrors()){ //검증처리시 오류가 발생하였다면
            //요청을 시도한 HTML로 되돌아간다. (GetMapping에 HTML파일로)
            return "insert";

        }
        log.info(storeDTO+"로 등록 요청");
        StoreDTO result = storeService.storeInsert(storeDTO);

        log.info("등록 후 검증");

        if (result != null) { //값이 있으면, 저장에 성공했으면
            redirectAttributes.addFlashAttribute("message", " 저장하였습니다.");
        } else { //저장을 실패했으면
            redirectAttributes.addFlashAttribute(
                    "message", "저장을 실패하였습니다."
            );
        }

        log.info("storeList로 맵핑 요청");
        return "redirect:/storeList";
    }
    /*------------------------------------------
    함수명 : storeUpdateForm(Integer idx)
    인  수 : 수정할 일련번호
    출  력 : 수정할 DTO를 전달
    내  용 : 일련번호로 해당 데이터를 조회해서 결과값을 HTML에 전달
    ----------------------------------------- */
    @Operation(summary = "총판 수정폼", description = "총판 수정폼으로 이동(update.html)")
    @GetMapping("/storeUpdate")
    public String storeUpdateForm(Integer idx, Model model, RedirectAttributes redirectAttributes) {
        log.info(idx+"으로 데이터 요청");

        StoreDTO read = storeService.storeRead(idx);

        log.info("결과 데이터 검증");
        if (read != null) {//수정할 데이터가 존재하면
            //수정시 수정할 DTO를 전달하므로 검증에 필요한 추가 작업이 없다.
            model.addAttribute("data", read);

            log.info("정상적 처리면 수정폼으로 이동");
            return "update";    //수정할 데이터가 있으면 수정폼으로 이동
        }

        //수정할 데이터가 존재하지 않으면
        redirectAttributes.addFlashAttribute("message", "해당 데이터가 존재하지 않습니다.");

        log.warning("비정상적 처리시 storeList맵핑으로 요청");
        return "redirect:/storeList"; //수정할 데이터가 존재하지 않으면 목록페이지로 이동
    }

    /*------------------------------------------
    함수명 : storeUpdateProc(StoreDTO storeDTO)
    인  수 : 수정한 DTO
    출  력 :  수정처리 후 결과 메시지
    설  명 : 수정할 데이터를 저장해서 결과 메시지를 가지고 list맵핑으로 이동
    ----------------------------------------- */
    @Operation(summary = "총판 수정처리", description = "총판 수정폼의 정보를 데이터베이스에 저장한다.")
    @PostMapping("/storeUpdate")
    public String storeUpdateProc(StoreDTO storeDTO, RedirectAttributes redirectAttributes){
        log.info(storeDTO.toString()+"수정 요청");
        StoreDTO result = storeService.storeUpdate(storeDTO);

        log.info("결과처리 검증");
        if(result != null) { //수정을 성공했을 때, 결과값이 비어있지 않으면
            redirectAttributes.addFlashAttribute("message", "수정하였습니다.");
        } else {
            redirectAttributes.addFlashAttribute("message", "수정을 실패하였습니다.");
        }

        log.info("storeList맵핑으로 요청");
        return "redirect:/storeList";
    }
    /*---------------------------------------------
    함수명 : storeReadProc(String idx)
    인  수 : 읽어올 일련번호
    출  력 : 조회된 DTO
    설  명 : 해당번호로 데이터베이스에서 조회하여 결과를 전달(HTML 상세페이지)
     -----------------------------------------------*/
    @Operation(summary = "상세보기", description = "지정 일련번호의 내용을 출력한다(read.html)")
    @GetMapping("/storeRead")
    public String storeReadPro(Integer idx, RedirectAttributes redirectAttributes, Model model) {
        StoreDTO result = storeService.storeRead(idx);
        if (result != null) { //조회한 결과가 존재하면
            model.addAttribute("data", result);
            return "read";
        }
        //조회한 결과가 존재하지 않으면
        redirectAttributes.addFlashAttribute("message", "해당하는 데이터가 존재하지 않습니다.");

        return "redirect:/storeList";
    }
    /*---------------------------------------------
    함수명 : storeListForm(Pageable pageable, String type, String keyword)
    인  수 : 조회할 페이지 정보, 분류대상, 검색 키워드
    출  력 : Page<StoreDTO>
    설  명 : 분류대상에 키워드로 조회한 해당 페이지 데이터를 전달
     -----------------------------------------------*/
    @Operation(summary = "총판 목록보기", description = "총판 데이터의 정보를 출력한다(list.html)")
    @GetMapping({"/", "/storeList"})
    public String storeList (
            @PageableDefault(page=1) Pageable pageable, //페이지정보, 페이지정보가 없으면 기 본값으로 1페이지로 설정
            @RequestParam(value="type", defaultValue = "") String type, //검색대상, 없으면 기본값은 0
            @RequestParam(value = "keyword", defaultValue = "") String keyword, //키워드, 없으면 기본값은 null
            Model model) {

        Page<StoreDTO> result = storeService.storeList(pageable, type, keyword);

        Map<String, Integer> pageInfo = pagenationUtil.pagination(result);

        model.addAttribute("list", result); // 데이터 전달
        model.addAllAttributes(pageInfo); //페이지 정보
        model.addAttribute("type", type); //검색분류
        model.addAttribute("keyword", keyword); //키워드

        return "list";
    }
     }

//13. templates안에 해당 HTML을 생성한다.

