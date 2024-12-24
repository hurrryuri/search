//9. Entity를 참고해서 사용할 변수로 구성
package com.example.search.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter

@ToString
public class StoreDTO {
    @Schema(description = "일련번호", example = "1")
    private Integer idx;

    @Schema(description = "총판이름", example = "ggg호텔")
    @NotBlank(message = "총판이름은 생략할 수 없습니다.")
    private String storeName;

    @Schema(description = "총판ID", example = "master")
    @NotBlank(message = "총판ID는 생략할 수 없습니다.")
    @Size(min=6, max=12, message="총판ID는 6~12자로 구성해야 합니다.")
    private String storeId;

    @Schema(description = "총판장ID", example = "master@gmail")
    @NotBlank(message = "총판장ID는 생략할 수 없습니다.")
    @Email(message="총판ID는 이메일형식으로 입력해야 합니다.")
    private String storeChiefId;

    @Schema(description = "총판장", example = "홍길동")
    private String storeChief;
    @Schema(description = "전화번호", example = "010-123-1234")
    private String storeTel;
    @Schema(description = "삭제여부(Y/N)", example = "Y")
    private String storeDel;
    private LocalDateTime regdate;
    private LocalDateTime moddate;

    //HTML에서 추가로 받을 변수를 DTO로 함께 받고자 하면 변수를 추가
    //Controller에서 개별변수를 받아서 사용
    //private String search;

}

//DTO까지 설계가 완료되면 1차 확인(단위테스트)