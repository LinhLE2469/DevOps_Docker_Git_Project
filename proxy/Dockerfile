FROM httpd:2.4-alpine
RUN apk add --no-cache curl
# Don't serve index.html - let proxy forward to frontend instead
# COPY index.html /usr/local/apache2/htdocs/
COPY httpd.conf /usr/local/apache2/conf/httpd.conf
