# Use OpenJDK 17
FROM eclipse-temurin:17-jdk-alpine

# Diretório da aplicação no container
WORKDIR /app

# Copia o pom.xml e os arquivos de dependências
COPY pom.xml .

# Baixa dependências antes de copiar o código-fonte
RUN ./mvnw dependency:go-offline -B

# Copia o código-fonte
COPY src ./src

# Compila a aplicação
RUN mvn spring-boot:run  

# Expõe a porta que o Spring Boot usa
EXPOSE 8081

# Comando para rodar a aplicação
ENTRYPOINT ["java", "-jar", "target/financas-0.0.1-SNAPSHOT.jar"]
