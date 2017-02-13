<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="text" indent="yes" omit-xml-declaration="yes" encoding="UTF-8" media-type="text/x-json"/>
	
	<xsl:strip-space elements="*"/>
	
    <xsl:template match="/">{
        <xsl:apply-templates select="*"/>}
    </xsl:template>
    
    <!-- Object or Element Property-->
    <xsl:template match="*">
        "<xsl:value-of select="name()"/>" : <xsl:call-template name="Properties"/>
    </xsl:template>

    <!-- Array Element -->
    <xsl:template match="*" mode="ArrayElement">
        <xsl:call-template name="Properties"/>
    </xsl:template>

    <!-- Object Properties -->
    <xsl:template name="Properties">
        <xsl:variable name="childName" select="name(*[1])"/>
        <xsl:choose>
            <xsl:when test="not(*|@*)">"<xsl:value-of select="."/>"</xsl:when>
            <xsl:when test="count(*[name()=$childName]) > 1">{ "<xsl:value-of select="$childName"/>" :[<xsl:apply-templates select="*" mode="ArrayElement"/>] }</xsl:when>
            <xsl:otherwise>{
                <xsl:apply-templates select="@*"/>
                <xsl:apply-templates select="*"/>
    }</xsl:otherwise>
        </xsl:choose>
        <xsl:if test="following-sibling::*">,</xsl:if>
    </xsl:template>

    <!-- Attribute Property -->
    <xsl:template match="@*">"@<xsl:value-of select="name()"/>" : "<xsl:value-of select="."/>"
    
		<xsl:choose>
			<xsl:when test="position()!=last() or ../position()!= last()">,</xsl:when>
			<xsl:otherwise></xsl:otherwise>
		</xsl:choose>
    </xsl:template>

</xsl:stylesheet>