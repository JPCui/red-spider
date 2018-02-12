<#macro pager prevPage prevMsg nextPage nextMsg lastPage lastMsg>
	<ul class="pager">
		<#if data.prevPage??>
			<li class="previous">
			<a href="${prevPage}" aria-label="${prevPage}">
			<span aria-hidden="true">${prevMsg}</span>
			</a>
			</li>
    	</#if>
		
		<li><a href="lastPage">${lastMsg}</a></li>
		
		<#if data.nextPage??>
			<li class="next">
			<a href="?page=${nextPage}" aria-label="${nextPage}">
			<span aria-hidden="true">${nextMsg}</span>
			</a>
			</li>
    	</#if>
	</ul>
</#macro>