### README

#### 1. TODO
- 전반적인 파일 탐색 구조 및 접근 권한 등등 파일 시스템 핸들러 개발
- CD/CI
- 멀티모듈화
- /error 화이트 라벨 페이지 생성
- HandlerMethodArgumentResolver 통한 어노테이션 기반 세션 get

#### 2. Branch
- master : 상용 배포 
- develope: 개발 테스트
- feature : 기능 개발

##### 2.1. 현재 개발중인 브랜치
- feature/error-with-discord-hook : 핸들링되지 않은 에러 발생 시 discord hook으로 에러 메시지 전달
- feature/session-cache : 디스코드 세션 캐시해 api 부담 완화
##### 2.2. 개발 완료된 브랜치
- feature/oauth : oauth 적용
  - request_uri 리팩터 등 기능 개선 필요
