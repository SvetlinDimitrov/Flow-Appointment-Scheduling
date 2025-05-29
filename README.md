# Flow Appointment Scheduling

## Project Presentation
Watch our project walkthrough: [Project Video](https://youtu.be/7JNJUH2in9w)

## Tech Stack

### Front-End
- **Framework**: React (18.3.1)
- **Routing**: React Router DOM
- **State Management**: React Query
- **UI Framework**: MUI (Material-UI)
- **Styling**: Emotion, Styled Components
- **Forms**: React Hook Form, Zod
- **Visualization**: React Chart.js 2
- **Calendar Integration**: React Big Calendar
- **Mapping**: React Leaflet
- **Notifications**: React Toastify
- **HTTP Client**: Axios
- **Date Handling**: Luxon, Moment.js
- **Security**: JWT Decode

### Back-End
- **Language**: Java 21
- **Framework**: Spring Boot (3.3.2)
- **Data Access**: Spring Data JPA
- **Database**: MySQL
- **Database Migration**: Flyway
- **Security**: Spring Security, JWT (JSON Web Tokens)
- **Validation**: Hibernate Validator
- **UI Template Engine**: Thymeleaf
- **API Documentation**: Springdoc OpenAPI
- **Mail Support**: Spring Boot Starter Mail
- **Mapping**: MapStruct
- **Password Policies**: Passay

### Additional Tools
- **Testing Frameworks**: JUnit, GreenMail, Testcontainers
- **Build Tool**: Maven
- **IDE Helpers**: Lombok
- **Build and Development**: Vite, ESLint, TypeScript

## How to Run the App

To start the application, ensure you have Docker and Docker Compose installed on your machine. Follow these steps:

1. **Run Docker Compose:**

   ```sh
   docker-compose up -d
 
2. **Access the Application:**

   - Open your browser and go to [http://localhost:3000](http://localhost:3000) to access the frontend.
   - Visit [http://localhost:5678](http://localhost:5678) to access phpMyAdmin for database management.

3. **Pre-Seeded Users:**

   The application is initialized with pre-seeded users for testing and development purposes. The default password for each user is `Password123!`.

   - **Staff Users:**  
     `staff1@flow.com` to `staff10@flow.com`
   
   - **Client Users:**  
     `client1@abv.bg` to `client20@abv.bg`
   
   - **Admin Users:**  
     `admin1@flow.com`, `admin2@flow.com`, `admin3@flow.com`

   Use the above emails with the password `Password123!` to log in and explore the application.
