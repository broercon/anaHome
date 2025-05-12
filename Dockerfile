# Use the official PostgreSQL image from the Docker Hub
FROM postgres:15

# Set environment variables (database name, user, password)
ENV POSTGRES_DB=your_db
ENV POSTGRES_USER=your_user
ENV POSTGRES_PASSWORD=your_password

# Optional: Copy SQL scripts to initialize the database
# COPY init.sql /docker-entrypoint-initdb.d/

# Expose PostgreSQL port
EXPOSE 5432