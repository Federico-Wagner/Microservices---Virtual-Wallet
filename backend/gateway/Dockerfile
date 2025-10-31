FROM nginx:1.23-alpine
COPY nginx.conf /etc/nginx/nginx.conf
# opcional: crear carpeta logs para que docker no marque como readonly
RUN mkdir -p /var/log/nginx
EXPOSE 8080
CMD ["nginx", "-g", "daemon off;"]
