# Stage 1: Build Stage
FROM node:18 as build

WORKDIR /app

# Copy the package.json and package-lock.json files
COPY package*.json ./

# Install dependencies
RUN npm install

# Copy the rest of your application code
COPY . .

# Build the application
RUN npm run build

# Stage 2: Production Stage
FROM node:18-slim

WORKDIR /app

# Copy build files from the previous stage
COPY --from=build /app/dist /app/dist

# Install serve globally
RUN npm install -g serve

# Expose port 3000
EXPOSE 3000

# Serve the app on port 3000
CMD ["serve", "-s", "dist", "-l", "3000"]
