<rss version="0.91">
  <channel>
    <title>TSAB Recent Recordings</title>
    <link>http://www.phon.ioc.ee/</link> 
    <description>Recently added recordings</description>
    <language>en-us</language>

<#list recentlyAdded as row>
    <item>
      <title><#if row.title?exists>${row.title}<#else>Untitled</#if></title>
      <link>${ctxpath}/p/play?trans=${row.id}</link>
      <description>Click on the link to listen to the transcribed recording</description>
    </item>
</#list>

  </channel>
</rss>
