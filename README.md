# Industrial Data Visualization Web Application

This Java-based web application is designed to offer robust data visualization capabilities, focusing on dynamic scatter charts. Utilizing the PrimeFaces framework for rich UI components and connecting to an MSSQL database for data management, this application allows users to generate and interact with scatter charts that update in real-time based on various parameters.

# Key Features:

Dynamic Scatter Charts: Create and view multiple scatter charts with real-time updates. Charts are generated dynamically based on user-selected parameters, providing flexibility in data analysis and visualization.

PrimeFaces Framework: The application integrates the PrimeFaces framework for a responsive and interactive user interface. This framework provides advanced UI components and enhances the overall user experience.

MSSQL Database Integration: Efficiently fetch and manage data from an MSSQL database. The application supports complex queries and handles large datasets to ensure accurate and timely visualizations.

Chart.js Integration: Leverage Chart.js for rendering scatter charts. This library is known for its high-quality, responsive charts and provides a seamless visual experience.

Customizable Parameters: Users can define and adjust multiple parameters to filter and customize the data displayed in the charts, enabling tailored insights and deeper analysis.

Interactive UI: The application features interactive elements such as tooltips and clickable data points, allowing users to explore the data in detail.

Real-Time Data Updates: The system supports real-time data updates, ensuring that the charts reflect the most current data available.

# Installation & Setup:

1. Clone the Repository:

bash
Copy code
git clone https://github.com/yourusername/your-repo-name.git

2. Set Up the Database:

Ensure MSSQL Server is running and create the necessary database schema as described in the database-setup.sql file.

3. Configure the Application:

Update the database connection details in application.properties or database-config.xml.

4. Build and Deploy:

Use Maven or your preferred build tool to compile and package the application.
Deploy the WAR file to a servlet container like Apache Tomcat.

5. Access the Application:

Open a web browser and navigate to http://localhost:8080/your-app-context to start using the application.

# Contributing:

Contributions are welcome! Please read the CONTRIBUTING.md for guidelines on how to contribute to this project.

# License:

This project is not licensed.

# Contact:

For any inquiries or support, please open an issue in the repository or contact [your email or contact information].
