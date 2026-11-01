<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="html" encoding="UTF-8" indent="yes"/>

    <xsl:template match="/wordList">
        <html>
            <head>
                <title>Словарь (REST XML View)</title>
                <style>
                    body { font-family: Arial, sans-serif; margin: 20px; }
                    table { border-collapse: collapse; width: 100%; }
                    th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
                    th { background-color: #f2f2f2; }
                    .nav { margin-bottom: 20px; }
                    .nav a { margin-right: 15px; text-decoration: none; color: #0066cc; }
                </style>
            </head>
            <body>
                <div class="nav">
                    <h1>RESTful Словарь</h1>
                    <a href="/api/words.xml">Список слов (XML)</a>
                    <a href="/api/words.json">Список слов (JSON)</a>
                </div>

                <h2>Список слов (<xsl:value-of select="count(word)"/> записей)</h2>
                <table>
                    <tr>
                        <th>Слово</th>
                        <th>Транскрипция</th>
                        <th>Часть речи</th>
                        <th>Категория</th>
                        <th>Переводы</th>
                    </tr>
                    <xsl:for-each select="word">
                        <tr>
                            <td><b><xsl:value-of select="englishWord"/></b></td>
                            <td><xsl:value-of select="phonetics"/></td>
                            <td><xsl:value-of select="partOfSpeech"/></td>
                            <td><xsl:value-of select="category"/></td>
                            <td>
                                <xsl:for-each select="translation">
                                    <xsl:value-of select="meaning"/>
                                    <xsl:if test="example"> (<i><xsl:value-of select="example"/></i>)</xsl:if>
                                    <br/>
                                </xsl:for-each>
                            </td>
                        </tr>
                    </xsl:for-each>
                </table>
            </body>
        </html>
    </xsl:template>
</xsl:stylesheet>