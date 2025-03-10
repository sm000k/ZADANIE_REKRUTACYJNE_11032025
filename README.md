# Project Overview

This project is a solution to the problem described in the email. It interacts with the GitHub API to fetch repositories, branches, and commits for a given user.

## Features

- Fetch user repositories
- Fetch branches of a repository
- Fetch the last commit of a branch

## Getting Started

### Prerequisites

- Java 11 or higher
- Maven

### Installation

1. Clone the repository:
    ```sh
    git clone https://github.com/your-repo/project1.git
    ```
2. Navigate to the project directory:
    ```sh
    cd project1/demo1
    ```
3. Build the project:
    ```sh
    mvn clean install
    ```

## Usage

Run the application:
```sh
mvn quarkus:dev
```

## Testing

The methods testing various elements of the application are contained in the `UserResourceTest.java` file. You can find the test file [here](src/test/java/org/acme/rest/UserResourceTest.java).

## Contact

For any questions or issues, please contact me at pw.lacki@gmail.com.
