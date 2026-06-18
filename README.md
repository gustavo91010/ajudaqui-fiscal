# ajudaqui-fiscal (v1.0.0)

Uma biblioteca Java para captura e normalização de documentos fiscais (NFCe/NFe) diretamente dos portais da SEFAZ.

## 📍 Roadmap de Cobertura

### Documentos Suportados
- [x] **NFC-e** (Modelo 65) - Nota Fiscal de Consumidor Eletrônica
- [ ] **NF-e** (Modelo 55) - Nota Fiscal Eletrônica
- [ ] **SAT/CFe** (Modelo 59) - Cupom Fiscal Eletrônico (SP)
- [ ] **MFE** (Modelo 65/59) - Módulo Fiscal Eletrônico (CE)

### Estados Suportados (NFC-e)
- [x] Bahia (BA)
- [x] Pernambuco (PE)
- [x] Paraná (PR)
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

### Configuração (Gradle)
```gradle
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.gustavopssilva:ajudaqui-fiscal:1.0.0'
}
```

### Configuração (Maven)
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.gustavopssilva</groupId>
    <artifactId>ajudaqui-fiscal</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Build Local (Maven Local)
Para utilizar a lib em outros projetos na sua máquina sem publicar:
```bash
./gradlew publishToMavenLocal
```

## 🛠️ Como Usar (Quick Start)

### Java
```java
FiscalParseService service = new FiscalParseService();
// Aceita URL da SEFAZ ou HTML bruto
UrlInput input = new UrlInput("https://nfce.sefaz.ba.gov.br/..."); 

// Parse para POJO
FiscalDocument doc = service.parse(input);
System.out.println("CNPJ: " + doc.getIssuer().getDocument());

// Parse para JSON
String json = service.parseToJson(input);
```

### Kotlin
```kotlin
val service = FiscalParseService()
val input = UrlInput("https://nfce.sefaz.pe.gov.br/...")

val document = service.parse(input)
println("Total: ${document.totals.totalValue}")
```

## 🏗️ Estrutura da Lib

- `com.ajudaqui.service`: Ponto de entrada (`FiscalParseService`).
- `com.ajudaqui.model`: Modelos de dados (POJOs).
- `com.ajudaqui.exception`: Exceções customizadas (`FiscalParseException`).
- `com.ajudaqui.internal`: Implementações internas (Parsers/Normalizers).

## 📄 Licença
Este projeto é de uso interno/pessoal.
