# HTML Analyzer

<sub>Solução desenvolvida em Java JDK 17. </sub>

## Objetivo:
A partir de uma URL ou arquivo local, obter o trecho de texto contido no nível mais profundo da estrutura HTML de seu conteúdo.

Por exemplo:
```
<html>
    <head>
        <title>
            Este é o título.
        </title>
    </head>
    <body>
        Este é o corpo.
    </body>
</html>
```
Na estrutura HTML acima, o trecho que será retornado é "Este é o título." (sem as aspas), porque está em 3 níveis de
profundidade (html > head > title)

Se dois ou mais trechos estiverem no nível máximo de profundidade do documento, o primeiro deles será retornado.

## Premissas:
A solução está baseada nas seguintes premissas:
1. O código HTML está dividido em linhas;
2. Cada linha é de apenas um dos seguintes tipos:
   1. Tag de abertura (exemplo: `<div>`)
   2. Tag de fechamento (exemplo: `</div>`)
   3. Trecho de texto (exemplo: “Este é o corpo.”)
3. Uma mesma linha não pode conter dois tipos de conteúdo;
4. Apenas elementos HTML com pares de tags de abertura e
   fechamento são utilizados (exemplo: `<div>` e `</div>`, mas não `<br/>`)
5. Tags de abertura não possuem atributos (contra-exemplo: `<a href=”link.html”>`).

## Retornos:
A solução gera apenas os seguintes tipos de output no console padrão:
1. A linha de trecho de texto identificado no HTML
2. `malformed HTML` em caso de estruturas HTML mal-formadas, isto é, não atendem às premissas.
3. `URL connection error` em caso de falha de conexão pela url.
4. Retorno de erro quando utilizando arquivo local e este não foi encontrado ou não é um HTML.

## Utilização:
### Por arquivo:
No início do arquivo existe a variável `filePath`. <br>
Esta variável deve conter o caminho relativo do arquivo (caso este esteja em uma pasta diferente à do projeto), ou o nome e extensão do arquivo (caso ele esteja na mesma pasta).<br>
Por padrão o projeto usa o arquivo `example.html` que se encontra na pasta do projeto.

Para realizar a execução do HtmlAnalyzer por arquivo, basta configurar o caminho do arquivo e executar o comando abaixo pelo terminal (lembando de estar dentro da pasta do projeto):
```
    java HtmlAnalyzer.java
```

### Por URL:
Para a execução por URL, basta acessar a pasta do projeto, pelo terminal, e executar o comando abaixo: <br>
```
    java HtmlAnalyzer [inserir-url-aqui]
```

