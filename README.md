# KIOT Project      
   Kweather (IoT Development Team)   



---
### **Data Base**     
+ MySQL v8.x   

### **Back End**     
+ Spring Boot v2.1.8    
+ Spring Security v5.1.6   
+ Gradle v5.6.2     
+ Lombok v1.18.8      
+ MyBatis v3.4.5 
+ Swagger Editor v2.7    
+ Liquibase v3.8.2        
+ apache.poi v3.11        

### **Front End**     
+ JSP, Thymeleaf
+ jQuery  
+ Ajax  
+ Datatable  
+ DateTimePicker   
+ OpenLayers v5.3   
+ AmChart Lib v4.x

### **Tool**  
+ STS(Spring Tool Suite) v3.9.8
+ MySQL Workbench v8.0

### **Etc** 
+ Atlassian JIRA (https://kiot2020.atlassian.net/secure/BrowseProjects.jspa)
+ ERCloud (https://www.erdcloud.com/d/QXbnPs4yzkc7sXCe2)

---
## Git Flow

### 브랜치 전략 (보류)

Git Flow전략을 사용하여 브랜치 관리.  
기능 개발은 feature 브랜치에서 진행하며, Pull Request에 리뷰를 진행한 후 merge를 진행.   

<p align="center">
 <img src="https://woowabros.github.io/img/2017-10-30/git-flow_overall_graph.png" width="400">
</p>

* `main` :: 배포시 사용하는 브랜치 .
* `develop` :: 다음 출시 버전을 개발하는 브랜치 . ( K.Weather, 이용 보류 )
	* 다음 릴리즈를 위해 언제든 배포될 수 있는 상태
* `feature` :: 기능을 개발하는 브랜치
	* 기능을 완성할 때 까지 유지하며, 완성시 `develop`브랜치로 merge ==> `main`브랜치로 merge
* `release` :: 릴리즈를 준비하는 브랜치 ( K.Weather, 이용 보류 )
* `hotfix` :: 배포 버전에서 생긴 문제로 긴급한 트러블 슈팅이 필요할 때 개발이 진행되는 브랜치

#### 참고 
 * 우린 Git-flow를 사용하고 있어요 : https://woowabros.github.io/experience/2017/10/30/baemin-mobile-git-branch-strategy.html

### Commit 메세지 규칙

* <타입>: <제목>의 형식으로 입력하며, 제목은 최대 50글자까지만 입력한다.
* 제목 첫 글자를 대문자로 입력한다.
* 제목은 명령문으로 작성한다.
* 제목과 본문을 한 줄 띄워 분리한다.
* 본문은 "어떻게"보다 "무엇을", "왜"를 설명한다.
* 본문에 여러 줄의 메시지를 작성할 땐 "-"로 구분한다.

#### 타입

* feat : 기능(새로운 기능)
* fix : 버그 ( 버그 수정)
* refactor : 리팩토링
* style : 스타일(코드 형식, 세미콜론 추가 등 비즈니스 로직에 영향이 없는 부분)
* docs : 문서 (문서 추가, 수정, 삭제)
* test : 테스트 ( 테스트 코드 추가, 수정, 삭제 등 비즈니스 로직에 영향이 없는 부분)
* chore : 기타 변경 사항(빌드 스크립트 수정 등)

#### 참고 
 * 좋은 커밋 메시지를 작성하기 위한 커밋 템플릿 만들어보기 : https://junwoo45.github.io/2020-02-06-commit_template/

