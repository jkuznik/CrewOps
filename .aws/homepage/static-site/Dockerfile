# Użycie lekkiego obrazu Nginx jako serwera dla plików statycznych
FROM nginx:alpine

# 1. Usuń domyślny plik konfiguracyjny Nginx
RUN rm /etc/nginx/conf.d/default.conf

# 2. Skopiuj niestandardowy plik nginx.conf, który zawiera regułę error_page
# Wymaga to, aby plik nginx.conf znajdował się w tym samym katalogu
COPY nginx.conf /etc/nginx/conf.d/default.conf

# 3. Usuń domyślną zawartość Nginx (jak wcześniej)
RUN rm -rf /usr/share/nginx/html/*

# 4. Skopiuj wszystkie pliki projektu (index.html, pl/, en/, css/, 404.html)
COPY . /usr/share/nginx/html

# Port 80
EXPOSE 80