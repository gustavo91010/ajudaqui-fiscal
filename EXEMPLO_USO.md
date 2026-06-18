# Como Usar a ajudaqui-fiscal

Esta biblioteca pode ser consumida por qualquer projeto JVM (Java, Kotlin, Scala, etc).

## 1. Instalação Local
Para que seu projeto encontre a lib na sua máquina, execute na pasta da lib:
```bash
./gradlew publishToMavenLocal
```

## 2. Configuração do Projeto Consumidor (Ex: Kotlin)

### `build.gradle.kts`
```kotlin
repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.github.gustavopssilva:ajudaqui-fiscal:1.0.0")
}
```

## 3. Código de Exemplo (Kotlin)

```kotlin
import com.ajudaqui.service.FiscalParseService
import com.ajudaqui.model.UrlInput

fun main() {
    val service = FiscalParseService()
    
    // URL de exemplo da SEFAZ-PE (XML)
    val input = UrlInput("https://nfce.sefaz.pe.gov.br/nfce-web/consultar?p=26260660832569000145650030000716601473652830|2|1|1|D6E...")
    
    try {
        // Opção 1: Retorno como Objeto Java/Kotlin
        val doc = service.parse(input)
        println("Empresa: ${doc.issuer.businessName}")

        // Opção 2: Retorno como JSON (String)
        val json = service.parseToJson(input)
        println("JSON Gerado: $json")
        
    } catch (e: Exception) {
        println("Erro ao processar: ${e.message}")
    }
}
```

## 4. Código de Exemplo (Java)

```java
import com.ajudaqui.service.FiscalParseService;
import com.ajudaqui.model.UrlInput;
import com.ajudaqui.model.FiscalDocument;

public class Main {
    public static void main(String[] args) {
        FiscalParseService service = new FiscalParseService();
        UrlInput input = new UrlInput("URL_DA_SEFAZ");

        // Retorno JSON direto
        String json = service.parseToJson(input);
        System.out.println(json);
    }
}
```
