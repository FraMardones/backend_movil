# --- ETAPA 1: Construcción (Build) ---
# Usamos una imagen de Maven (basada en Java 21) para construir el proyecto
FROM maven:3.9.7-eclipse-temurin-21 AS build

WORKDIR /build

# Copiamos el pom.xml y descargamos las dependencias
COPY pom.xml .
RUN mvn dependency:go-offline

# Copiamos todo el código fuente y lo compilamos
COPY src ./src
RUN mvn clean package -DskipTests

# --- ETAPA 2: Ejecución (Run) ---
# Usamos la imagen base limpia de Java 21
FROM eclipse-temurin:21-jdk

# --- ¡CAMBIO 1: Instalar 'unzip' ---
# Necesitamos permisos de root para instalarlo
USER root
RUN apt-get update && apt-get install -y unzip && rm -rf /var/lib/apt/lists/*

# Volvemos al directorio de trabajo
WORKDIR /app

# --- ¡CAMBIO 2: Copiar el ZIP y DESCOMPRIMIRLO en /wallet ---
# Copiamos el zip (asumiendo que está en src/main/resources/wallet/Wallet_base1.zip)
# La ruta de origen (COPY) mira tus archivos locales, no la Etapa 1
COPY src/main/resources/wallet/Wallet_base1.zip /app/wallet.zip

# Creamos el directorio /wallet y descomprimimos los archivos allí
RUN mkdir /wallet
RUN unzip /app/wallet.zip -d /wallet

# Copiamos el .jar de la etapa 1
COPY --from=build /build/target/PatasyColas-0.0.1-SNAPSHOT.jar app.jar

# Exponemos el puerto
ENV PORT=8080
EXPOSE 8080

# --- ¡CAMBIO 3: Apuntar al directorio /wallet (donde están los archivos extraídos) ---
ENTRYPOINT ["java", "-Doracle.net.tns_admin=/wallet", "-jar", "/app/app.jar"]