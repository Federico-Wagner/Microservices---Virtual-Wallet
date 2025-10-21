FROM golang:1.22-alpine AS build
WORKDIR /app
COPY . .
RUN go mod download
RUN go build -o notifications cmd/main.go

FROM alpine:3.18
WORKDIR /app
COPY --from=build /app/notifications .
CMD ["./notifications"]
