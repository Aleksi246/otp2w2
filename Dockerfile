FROM maven:3.9.6-eclipse-temurin-21 AS build

ENV DISPLAY=host.docker.internal:0.0

# Install only required libraries (NO MAVEN HERE)
RUN apt-get update && \
    apt-get install -y wget unzip libgtk-3-0 libgbm1 libx11-6 fonts-noto fonts-noto-cjk fontconfig && \
    fc-cache -f && \
    cp /usr/share/fonts/opentype/noto/NotoSansCJK-Regular.ttc /usr/lib/jvm/temurin-21-jdk-amd64/lib/fonts/ 2>/dev/null || true && \
    apt-get clean

# Download JavaFX SDK
RUN wget https://download2.gluonhq.com/openjfx/21/openjfx-21_linux-x64_bin-sdk.zip -O /tmp/openjfx.zip && \
    unzip /tmp/openjfx.zip -d /opt && \
    rm /tmp/openjfx.zip

WORKDIR /app

COPY target/demo.jar .


CMD ["java", "--module-path", "/opt/javafx-sdk-21/lib", "--add-modules", "javafx.controls,javafx.fxml", "-jar", "demo.jar"]