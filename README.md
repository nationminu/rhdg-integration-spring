# integration_with_the_spring_framework

## Red Hat Data Grid 8 Supported Configurations 
https://access.redhat.com/articles/4933551

## Red Hat Data Grid 7 Supported Configurations
https://access.redhat.com/articles/2435931

## Integration with Spring Framework 4 
> Redhat Data Grid 7.2 <BR>
https://access.redhat.com/documentation/en-us/red_hat_data_grid/7.2/html/developer_guide/integration_with_the_spring_framework

> Infinispan 9.4 <BR>
https://infinispan.org/docs/9.4.x/user_guide/user_guide.html#integrations_jpa_hibernate
https://github.com/infinispan/infinispan/blob/9.4.x/spring/spring5/spring5-remote/src/main/java/org/infinispan/spring/remote/provider/SpringRemoteCacheManagerFactoryBean.java

 > Spring Framework Documentation <BR>
https://docs.spring.io/spring-framework/docs/current/reference/html/integration.html
https://docs.spring.io/spring-framework/docs/4.3.25.RELEASE/spring-framework-reference/htmlsingle/#cache


## Integration with Spring Framework 5 
> Infinispan 12.1 <BR>
https://infinispan.org/docs/dev/titles/integrating/integrating.html 
https://github.com/infinispan/infinispan/blob/12.1.x/spring/spring5/spring5-remote/src/main/java/org/infinispan/spring/remote/provider/SpringRemoteCacheManagerFactoryBean.java

 > Spring Framework Documentation <BR>
https://docs.spring.io/spring-framework/docs/current/reference/html/integration.html


## Integration with Spring Boot
> Redhat Data Grid 8.2 <BR>
https://access.redhat.com/documentation/en-us/red_hat_data_grid/8.2/html/data_grid_spring_boot_starter/index


# Integration with egovframework 3.10 simple homepage

## 전자정부프레임워크 3.10 simple homepage
> https://www.egovframe.go.kr/home/main.do

## Spring cacheManager
> src/main/resources/egovframework/spring/com/context-cache.xml
```
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:cache="http://www.springframework.org/schema/cache"
	xmlns:p="http://www.springframework.org/schema/p"
	xsi:schemaLocation="
        http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/cache http://www.springframework.org/schema/cache/spring-cache.xsd">

	<cache:annotation-driven />

	<bean id="cacheManager"
		class="org.infinispan.spring.provider.SpringRemoteCacheManagerFactoryBean"
		p:configuration-properties-file-location="classpath:/egovframework/egovProps/hotrod-client.properties" />

</beans>
```

> src/main/resources/egovframework/egovProps/hotrod-client.properties
```
# List Infinispan servers by IP address or hostname at port 11222.
infinispan.client.hotrod.server_list=datagrid1:11222;datagrid2:11222;datagrid3:11222;

# Use BASIC client intelligence.
infinispan.client.hotrod.client_intelligence=BASIC

# Authentication
infinispan.client.hotrod.use_auth=true
infinispan.client.hotrod.auth_username=admin
infinispan.client.hotrod.auth_password=admin
infinispan.client.hotrod.auth_realm=default
infinispan.client.hotrod.sasl_mechanism=SCRAM-SHA-512

infinispan.spring.operation.read.timeout=500
infinispan.spring.operation.read.timeout=700
```

> Exception in JBoss <br>
> java.lang.NoClassDefFoundError: sun/reflect/ReflectionFactory
> WEB-INF/jboss-deployment-structure.xml
```
<jboss-deployment-structure xmlns="urn:jboss:deployment-structure:1.1">
    <deployment>
        <dependencies>
            <system export="true">
                <paths>
                    <path name="sun/reflect"/>
                </paths>
            </system>
        </dependencies>
    </deployment>
</jboss-deployment-structure>
```

# Cache 사용하기

## 1. 메인 공지사항 Cache Service.ServiceImpl 추가
> /simple/src/main/java/egovframework/let/cop/bbs/service/impl/EgovBBSManageService.java <BR>
> /simple/src/main/java/egovframework/let/cop/bbs/service/impl/EgovBBSManageServiceImpl.java  <BR>
> selectBoardArticles -> selectBoardCache  <BR>

> @Cacheable(cacheNames = "bbs", key = "#boardVO") 어노테이션 추가 <BR>
> * cacheName : Datagrid cache Name 
> * key : cache key
```
    @Cacheable(cacheNames = "bbs", key = "#boardVO")
	public Map<String, Object> selectBoardCache(BoardVO boardVO, String attrbFlag) throws Exception {

		long start = System.currentTimeMillis();

		List<BoardVO> list = bbsMngDAO.selectBoardArticleList(boardVO);
		List<BoardVO> result = new ArrayList<BoardVO>();

		if ("BBSA01".equals(attrbFlag)) {
			// 유효게시판 임
			String today = EgovDateUtil.getToday();

			BoardVO vo;
			Iterator<BoardVO> iter = list.iterator();
			while (iter.hasNext()) {
				vo = (BoardVO) iter.next();

				if (!"".equals(vo.getNtceBgnde()) || !"".equals(vo.getNtceEndde())) {
					if (EgovDateUtil.getDaysDiff(today, vo.getNtceBgnde()) > 0
							|| EgovDateUtil.getDaysDiff(today, vo.getNtceEndde()) < 0) {
						// 시작일이 오늘날짜보다 크거나, 종료일이 오늘 날짜보다 작은 경우
						vo.setIsExpired("Y");
					}
				}
				result.add(vo);
			}
		} else {
			result = list;
		}

		int cnt = bbsMngDAO.selectBoardArticleListCnt(boardVO);

		Map<String, Object> map = new HashMap<String, Object>();

		long end = System.currentTimeMillis();

		System.out.println("------------ CacheVo : " + result + ", " + (end - start) + " ms");

		map.put("resultList", result);
		map.put("resultCnt", Integer.toString(cnt));

		return map;
	}
```

## 2. 
> Controller 공지사항 Cache 서비스 호출 <BR>
> /simple/src/main/java/egovframework/let/main/web/EgovMainController.java
```
		// map = bbsMngService.selectBoardCache(boardVO, "BBSA02");
        ->
		map = bbsMngService.selectBoardCache(boardVO, "BBSA02");
		model.addAttribute("notiList", map.get("resultList"));
```

> 최초 메인 메이지 접속시 디비에서 VO 정보 호출 두번째 호출부터 확인되지 않음
```
------------ CacheVo : [egovframework.let.cop.bbs.service.BoardVO@7874ad9d[searchBgnDe=,searchCnd=,searchEndDe=,searchWrd=,sortOrdr=0,searchUseYn=,pageIndex=1,pageUnit=10,pageSize=10,firstIndex=1,lastIndex=1,recordCountPerPage=10,rowNo=0,frstRegisterNm=관리자,lastUpdusrNm=,isExpired=N,parntsSortOrdr=,parntsReplyLc=,bbsTyCode=,bbsAttrbCode=,bbsNm=,fileAtchPosblAt=,posblAtchFileNumber=0,replyPosblAt=,plusCount=false,subPageIndex=,atchFileId=<null>,bbsId=BBSMSTR_AAAAAAAAAAAA,frstRegisterId=USRCNFRM_00000000000,frstRegisterPnttm=2021-07-14,lastUpdusrId=,lastUpdusrPnttm=,ntceBgnde=10000101,ntceEndde=99991231,ntcrId=,ntcrNm=,nttCn=,nttId=1,nttNo=0,nttSj=홈페이지 샘플공지1,parnts=0,password=,inqireCo=0,replyAt=N,replyLc=0,sortOrdr=0,useAt=Y,ntceEnddeView=,ntceBgndeView=], egovframework.let.cop.bbs.service.BoardVO@5c0f9b0e[searchBgnDe=,searchCnd=,searchEndDe=,searchWrd=,sortOrdr=0,searchUseYn=,pageIndex=1,pageUnit=10,pageSize=10,firstIndex=1,lastIndex=1,recordCountPerPage=10,rowNo=0,frstRegisterNm=관리자,lastUpdusrNm=,isExpired=N,parntsSortOrdr=,parntsReplyLc=,bbsTyCode=,bbsAttrbCode=,bbsNm=,fileAtchPosblAt=,posblAtchFileNumber=0,replyPosblAt=,plusCount=false,subPageIndex=,atchFileId=<null>,bbsId=BBSMSTR_AAAAAAAAAAAA,frstRegisterId=USRCNFRM_00000000000,frstRegisterPnttm=2021-07-14,lastUpdusrId=,lastUpdusrPnttm=,ntceBgnde=10000101,ntceEndde=99991231,ntcrId=,ntcrNm=,nttCn=,nttId=2,nttNo=0,nttSj=홈페이지 샘플공지2,parnts=0,password=,inqireCo=1,replyAt=N,replyLc=0,sortOrdr=0,useAt=Y,ntceEnddeView=,ntceBgndeView=], egovframework.let.cop.bbs.service.BoardVO@6db9ceab[searchBgnDe=,searchCnd=,searchEndDe=,searchWrd=,sortOrdr=0,searchUseYn=,pageIndex=1,pageUnit=10,pageSize=10,firstIndex=1,lastIndex=1,recordCountPerPage=10,rowNo=0,frstRegisterNm=관리자,lastUpdusrNm=,isExpired=N,parntsSortOrdr=,parntsReplyLc=,bbsTyCode=,bbsAttrbCode=,bbsNm=,fileAtchPosblAt=,posblAtchFileNumber=0,replyPosblAt=,plusCount=false,subPageIndex=,atchFileId=<null>,bbsId=BBSMSTR_AAAAAAAAAAAA,frstRegisterId=USRCNFRM_00000000000,frstRegisterPnttm=2021-07-14,lastUpdusrId=,lastUpdusrPnttm=,ntceBgnde=10000101,ntceEndde=99991231,ntcrId=,ntcrNm=,nttCn=,nttId=3,nttNo=0,nttSj=홈페이지 샘플공지3,parnts=0,password=,inqireCo=0,replyAt=N,replyLc=0,sortOrdr=0,useAt=Y,ntceEnddeView=,ntceBgndeView=], egovframework.let.cop.bbs.service.BoardVO@2ba7596a[searchBgnDe=,searchCnd=,searchEndDe=,searchWrd=,sortOrdr=0,searchUseYn=,pageIndex=1,pageUnit=10,pageSize=10,firstIndex=1,lastIndex=1,recordCountPerPage=10,rowNo=0,frstRegisterNm=관리자,lastUpdusrNm=,isExpired=N,parntsSortOrdr=,parntsReplyLc=,bbsTyCode=,bbsAttrbCode=,bbsNm=,fileAtchPosblAt=,posblAtchFileNumber=0,replyPosblAt=,plusCount=false,subPageIndex=,atchFileId=<null>,bbsId=BBSMSTR_AAAAAAAAAAAA,frstRegisterId=USRCNFRM_00000000000,frstRegisterPnttm=2021-07-14,lastUpdusrId=,lastUpdusrPnttm=,ntceBgnde=10000101,ntceEndde=99991231,ntcrId=,ntcrNm=,nttCn=,nttId=4,nttNo=0,nttSj=홈페이지 샘플공지4,parnts=0,password=,inqireCo=0,replyAt=N,replyLc=0,sortOrdr=0,useAt=Y,ntceEnddeView=,ntceBgndeView=], egovframework.let.cop.bbs.service.BoardVO@1775a0e3[searchBgnDe=,searchCnd=,searchEndDe=,searchWrd=,sortOrdr=0,searchUseYn=,pageIndex=1,pageUnit=10,pageSize=10,firstIndex=1,lastIndex=1,recordCountPerPage=10,rowNo=0,frstRegisterNm=관리자,lastUpdusrNm=,isExpired=N,parntsSortOrdr=,parntsReplyLc=,bbsTyCode=,bbsAttrbCode=,bbsNm=,fileAtchPosblAt=,posblAtchFileNumber=0,replyPosblAt=,plusCount=false,subPageIndex=,atchFileId=<null>,bbsId=BBSMSTR_AAAAAAAAAAAA,frstRegisterId=USRCNFRM_00000000000,frstRegisterPnttm=2021-07-14,lastUpdusrId=,lastUpdusrPnttm=,ntceBgnde=10000101,ntceEndde=99991231,ntcrId=,ntcrNm=,nttCn=,nttId=5,nttNo=0,nttSj=홈페이지 샘플공지5,parnts=0,password=,inqireCo=0,replyAt=N,replyLc=0,sortOrdr=0,useAt=Y,ntceEnddeView=,ntceBgndeView=]], 121 ms
```


> 메인페이지 최초 호출시 디비에서 호회후 Datagrid 에 입력 Datagrid bbs Cache 조회시 BoardVO 오브젝트 확인.
Datagrid bbs Cache 가 삭제되기 전까지 DB 호출 없이 Datagrid 에서 호출함.
