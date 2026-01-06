# Hunter Plugin 프로젝트 피드백

## 📊 개요
Minecraft Paper 플러그인 개발 프로젝트로, 팀 기반 Hunter 게임을 구현하고 있습니다. Clean Architecture 패턴을 적용한 Kotlin 코드베이스입니다.

---

## 1. 현실적인 개발 시간 추정

### 현재 상태 분석
- **코드 규모**: 약 18개 Kotlin 파일, 추정 1,500-2,000 라인
- **구현 완성도**: 약 70-80% (기본 기능 구현 완료, 예외 처리 및 테스트 부족)
- **아키텍처**: Clean Architecture 적용 (Services, UseCases, Listeners, Commands 분리)

### 개발 시간 추정 (1인 개발 기준)

#### 기본 기능 완성 (완료됨)
- **실제 소요 시간 추정**: 20-30시간
- 핵심 기능 구현: 팀 관리, 플레이어 상태 관리, Hunter 추적 시스템

#### 남은 작업 및 예상 시간
1. **예외 처리 및 안정성 강화**: 4-6시간
   - 파일 I/O 예외 처리
   - null 안전성 보완
   - 동시성 문제 해결

2. **테스트 코드 작성**: 8-12시간
   - 단위 테스트 (Services, UseCases)
   - 통합 테스트 (Commands, Listeners)
   - Mockito/MockBukkit 활용

3. **코드 리팩토링**: 3-4시간
   - 버그 수정 (TeamService.assign 중복 제거 로직)
   - 코드 중복 제거
   - 주석 및 문서화

4. **운영 기능 추가**: 4-6시간
   - onDisable 구현 (데이터 저장)
   - 로깅 시스템
   - 설정 파일 관리

5. **문서화**: 2-3시간
   - README 작성
   - API 문서
   - 사용자 가이드

**총 추가 예상 시간: 21-31시간**

### 프로젝트 완성까지 총 예상 시간
- **현재까지**: 20-30시간
- **추가 필요**: 21-31시간
- **총 소요 시간**: **41-61시간** (약 1.5-2개월, 주 10시간 기준)

---

## 2. 코드 완성도 향상을 위한 개선 사항

### 🔴 긴급 (Critical)

#### 2.1 예외 처리 부족
**문제점:**
- 파일 I/O 작업에 try-catch 없음 (`TeamService`, `PlayerStateService`)
- 서버 종료 시 데이터 손실 가능성
- 파일 로드 실패 시 조용히 실패 (사용자 알림 없음)

**개선 방안:**
```kotlin
// TeamService.kt
fun saveTeams() {
    try {
        config.set("players", null)
        teamMap.forEach { (uuid, team) ->
            config.set("players.$uuid", team.name)
        }
        config.save(file)
        plugin.logger.info("팀 데이터 저장 완료")
    } catch (e: Exception) {
        plugin.logger.severe("팀 데이터 저장 실패: ${e.message}")
        e.printStackTrace()
    }
}
```

#### 2.2 onDisable 미구현
**문제점:**
- 플러그인 비활성화 시 데이터 저장 안 됨
- 서버 재시작 시 데이터 손실

**개선 방안:**
```kotlin
// Hunter.kt
override fun onDisable() {
    logger.info("Hunter Plugin is Deactivating")
    
    // 데이터 저장
    teamService.saveTeams()
    playerStateService.saveStates()
    
    // 스케줄러 취소
    // trackingSchedulers.cancel()
    
    logger.info("Hunter Plugin is Deactivated")
}
```

#### 2.3 TeamService.assign 버그
**문제점:**
```kotlin
// 52번째 줄: 중복된 제거 로직
teamMap[uuid]?.let { teamMembers[it]?.remove(uuid) }  // 불필요한 중복
teamMap[uuid] = team
```

**개선 방안:**
```kotlin
fun assign(player: Player, team: TeamType) {
    val uuid = player.uniqueId
    
    // 기존 팀 제거 (한 번만)
    teamMap[uuid]?.let { oldTeam ->
        teamMembers[oldTeam]?.remove(uuid)
    }
    
    // 새 팀 등록
    teamMap[uuid] = team
    teamMembers.getOrPut(team) { mutableSetOf() }.add(uuid)
    
    // Scoreboard 업데이트
    val teamName = team.name
    var scoreboardTeam = scoreboard.getTeam(teamName) 
        ?: scoreboard.registerNewTeam(teamName)
    scoreboardTeam.addEntry(player.name)
}
```

### 🟡 중요 (Important)

#### 2.4 로깅 부족
**문제점:**
- 중요한 작업에 로깅 없음
- 디버깅 어려움
- 운영 중 문제 추적 불가

**개선 방안:**
- 모든 Service 메서드에 적절한 로그 레벨 추가
- INFO: 일반 작업 (팀 배정, 상태 변경)
- WARNING: 예상치 못한 상황 (팀 없음, 플레이어 오프라인)
- SEVERE: 오류 상황

#### 2.5 주석 처리된 코드 정리
**문제점:**
- `Hunter.kt`에 주석 처리된 코드 다수 (78-139줄)
- `Main.kt` 파일 미사용
- `GameStateService.kt`에 불명확한 주석

**개선 방안:**
- 주석 처리된 코드는 Git 히스토리로 관리하고 삭제
- `Main.kt` 삭제 또는 테스트 코드로 활용
- 불명확한 주석 제거 또는 명확하게 수정

#### 2.6 하드코딩된 경로
**문제점:**
```kotlin
// build.gradle.kts 52번째 줄
into("D:hunter-server/plugins")  // 절대 경로 하드코딩
```

**개선 방안:**
```kotlin
// 환경 변수 또는 프로퍼티 파일 사용
val serverPath = project.findProperty("server.path") 
    ?: "${project.rootDir}/server/plugins"
into(serverPath)
```

#### 2.7 null 안전성
**문제점:**
- `HunterTrackingService.getNearestEnemy`에서 Player가 null일 수 있음
- `TeamService.listAll`에서 OfflinePlayer.name이 null일 수 있음

**개선 방안:**
- Elvis 연산자 적극 활용
- 명확한 fallback 값 제공

### 🟢 개선 (Enhancement)

#### 2.8 의존성 주입
**현재:** 수동으로 서비스 주입
**개선:** Koin 또는 Kodein 같은 DI 프레임워크 도입 고려

#### 2.9 설정 파일 관리
- `config.yml` 추가하여 게임 설정 관리
- 팀 이름, 게임 시간, 추적 간격 등 설정 가능하도록

#### 2.10 성능 최적화
- `Bukkit.getOnlinePlayers().forEach` 반복 사용 시 최적화
- 캐싱 전략 도입 (예: 팀 멤버 조회)

---

## 3. 프로젝트 관리

### 3.1 버전 관리 (Git)

#### 현재 상태
- `.gitignore` 파일 없음
- build 산출물, IDE 설정 파일이 추적될 가능성

#### 개선 방안
`.gitignore` 파일 생성:
```
# Gradle
.gradle/
build/
!gradle/wrapper/gradle-wrapper.jar
*.jar
!**/src/main/**/resources/**/*.jar

# IDE
.idea/
*.iml
*.iws
*.ipr
.vscode/
.classpath
.project
.settings/

# OS
.DS_Store
Thumbs.db

# Plugin specific
server/
*.log
```

### 3.2 프로젝트 구조

#### 현재 구조 (우수함 ✅)
```
lib/src/main/kotlin/hunter/papermc/testplugin/
├── commands/          # 커맨드 핸들러
├── components/        # 도메인 모델 (enum 등)
├── listeners/         # 이벤트 리스너
├── recipes/           # 레시피 정의
├── schedulers/        # 스케줄러
├── services/          # 비즈니스 로직
└── usecases/          # 유즈케이스
```

**장점:**
- 계층 분리 명확
- 단일 책임 원칙 준수
- 확장 가능한 구조

**개선 제안:**
- `components`를 `domain` 또는 `model`로 리네이밍 고려
- `utils` 패키지 추가 (공통 유틸리티 함수)

### 3.3 의존성 관리

#### 현재 상태
- `build.gradle.kts`에 버전 하드코딩
- `gradle/libs.versions.toml`은 생성되어 있으나 미사용

#### 개선 방안
```kotlin
// build.gradle.kts
dependencies {
    val paperVersion = "1.21.4-R0.1-SNAPSHOT"
    compileOnly("io.papermc.paper:paper-api:$paperVersion")
    implementation(kotlin("stdlib"))
    
    // 테스트 추가
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testImplementation("com.github.seeseemelk:MockBukkit-v1.21:3.145.0")
}
```

### 3.4 문서화

#### README.md 개선 필요
현재: `# hunter-game` 만 있음

**추천 구조:**
```markdown
# Hunter Plugin

## 설명
팀 기반 Hunter 게임 플러그인

## 기능
- 팀 관리 (Yellow/Blue)
- Hunter 추적 시스템
- 플레이어 상태 관리

## 설치
1. Paper 1.21.4 서버 필요
2. plugins 폴더에 jar 파일 복사

## 사용법
/team <yellow|blue> <player> - 팀 배정
/teamlist - 팀 목록 확인

## 개발
- Kotlin 1.9.21
- Java 21
- Gradle 8.8

## 라이선스
...
```

### 3.5 이슈 관리
- GitHub Issues 또는 프로젝트 관리 도구 활용
- 버그, 기능 요청, 개선 사항 추적

---

## 4. 프로젝트 운영

### 4.1 배포 프로세스

#### 현재
- `copyToServer` 태스크로 수동 배포
- 하드코딩된 경로

#### 개선 방안
1. **자동화된 빌드 스크립트**
   ```bash
   # deploy.sh
   ./gradlew clean build shadowJar
   cp lib/build/libs/Hunter-1.0.jar /path/to/server/plugins/
   ```

2. **CI/CD 파이프라인** (선택사항)
   - GitHub Actions로 자동 빌드
   - 테스트 통과 시 자동 배포

### 4.2 모니터링 및 로깅

#### 로깅 전략
```kotlin
// 로그 레벨 구분
logger.info("일반 정보성 로그")
logger.warning("경고 로그")
logger.severe("에러 로그")
```

#### 모니터링 포인트
- 플러그인 활성화/비활성화
- 팀 배정/제거
- 파일 저장 실패
- 예외 발생

### 4.3 데이터 관리

#### 백업 전략
- 정기적으로 `data` 폴더 백업
- 서버 재시작 전 데이터 저장 확인

#### 마이그레이션
- 버전 업그레이드 시 데이터 형식 변경 대비
- 마이그레이션 스크립트 준비

### 4.4 성능 모니터링

#### 주의할 부분
- `Bukkit.getOnlinePlayers()` 반복 호출
- 파일 I/O 빈도 (저장 타이밍 최적화)
- 메모리 사용량 (Player 객체 캐싱)

### 4.5 사용자 지원

#### 필요 문서
- 플레이어 가이드
- 관리자 가이드
- 트러블슈팅 가이드

#### 피드백 채널
- GitHub Issues
- Discord 서버 (선택사항)

---

## 5. 우선순위별 액션 아이템

### Phase 1: 안정성 (1주)
1. ✅ 예외 처리 추가 (파일 I/O)
2. ✅ onDisable 구현
3. ✅ TeamService 버그 수정
4. ✅ .gitignore 추가

### Phase 2: 품질 (2주)
5. ✅ 로깅 시스템 구축
6. ✅ 주석 처리된 코드 정리
7. ✅ null 안전성 강화
8. ✅ README 작성

### Phase 3: 테스트 (2주)
9. ✅ 단위 테스트 작성
10. ✅ 통합 테스트 작성
11. ✅ 테스트 커버리지 70% 이상

### Phase 4: 운영 준비 (1주)
12. ✅ 설정 파일 시스템
13. ✅ 배포 프로세스 정리
14. ✅ 문서화 완료

---

## 6. 종합 평가

### 강점 ✅
1. **Clean Architecture 적용**: 코드 구조가 명확하고 확장 가능
2. **Kotlin 활용**: 현대적인 언어 사용
3. **계층 분리**: Services, UseCases, Commands 분리
4. **기본 기능 완성**: 핵심 게임플레이 구현

### 개선 필요 📝
1. **예외 처리**: 안정성 강화 필요
2. **테스트 코드**: 전무 상태
3. **문서화**: README 및 코드 주석 부족
4. **운영 기능**: onDisable, 로깅 등 부족

### 전체 완성도
- **현재**: 70-75%
- **완성까지**: 약 30-40시간 추가 작업 필요
- **운영 준비까지**: 약 50-60시간 추가 작업 필요

---

## 7. 권장 사항

1. **단계적 접근**: Phase별로 우선순위를 정해 체계적으로 진행
2. **테스트 우선**: 새로운 기능 추가 전 테스트 코드 작성 습관화
3. **코드 리뷰**: 가능하다면 동료와 코드 리뷰 진행
4. **사용자 피드백**: 초기 버전 배포 후 사용자 피드백 수집
5. **지속적 개선**: 완벽을 추구하기보다 동작하는 버전을 빠르게 배포하고 개선

---

*이 피드백은 2024년 기준으로 작성되었으며, 프로젝트 진행 상황에 따라 우선순위가 변경될 수 있습니다.*

