# ajudaqui-fiscal (v1.0.0)

Uma biblioteca Java para captura e normalização de documentos fiscais (NFCe/NFe) diretamente dos portais da SEFAZ.

## 📍 Roadmap de Cobertura

### Documentos Suportados
- [x] **NFC-e** (Modelo 65) - Nota Fiscal de Consumidor Eletrônica
- [ ] **NF-e** (Modelo 55) - Nota Fiscal Eletrônica
- [ ] **SAT/CFe** (Modelo 59) - Cupom Fiscal Eletrônico (SP)
- [ ] **MFE** (Modelo 65/59) - Módulo Fiscal Eletrônico (CE)

### Estados Suportados (NFC-e)
- [x] Pernambuco (PE)
- [ ] São Paulo (SP)
- [ ] Minas Gerais (MG)
- [ ] Rio de Janeiro (RJ)
- [ ] Outros...

## 🚀 Características

- **Agnóstica**: Sem dependência de frameworks (Quarkus, Spring, etc).
- **Leve**: Depende apenas de Jackson (JSON), JSoup (HTML/XML) e SLF4J (Logging).
- **Java 11+**: Compatível com projetos Java e Kotlin modernos.
- **Saída Pronta**: Opção de retorno como objeto (POJO) ou String JSON formatada.

## 📦 Instalação


### Configuração no Projeto Consumidor (Gradle)
```gradle
repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation 'com.ajudaqui:ajudaqui-fiscal:1.0.0'
}
```

### Configuração no Projeto Consumidor (Maven)
```xml
<dependency>
    <groupId>com.ajudaqui</groupId>
    <artifactId>ajudaqui-fiscal</artifactId>
    <version>1.0.0</version>
</dependency>
```

## 🛠️ Como Usar

### Exemplo em Java
```java
import com.ajudaqui.service.FiscalParseService;
import com.ajudaqui.model.UrlInput;

public class Main {
    public static void main(String[] args) {
        FiscalParseService service = new FiscalParseService();
        UrlInput input = new UrlInput("https://nfce.sefaz.pe.gov.br/...");

        // Parse direto para JSON
        String json = service.parseToJson(input);
        System.out.println(json);
    }
}
```

### Exemplo em Kotlin
```kotlin
import com.ajudaqui.service.FiscalParseService
import com.ajudaqui.model.UrlInput

fun main() {
    val service = FiscalParseService()
    val input = UrlInput("https://nfce.sefaz.pe.gov.br/...")

    val document = service.parse(input)
    println("Empresa: ${document.issuer.businessName}")
}
```

## 🏗️ Estrutura da Lib

- `com.ajudaqui.service`: Ponto de entrada (`FiscalParseService`).
- `com.ajudaqui.model`: Modelos de dados (POJOs).
- `com.ajudaqui.exception`: Exceções customizadas (`FiscalParseException`).
- `com.ajudaqui.internal`: Implementações internas (Parsers/Normalizers).

## 📄 Licença
Este projeto é de uso interno/pessoal.
