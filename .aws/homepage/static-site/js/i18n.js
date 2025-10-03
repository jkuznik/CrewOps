document.addEventListener('DOMContentLoaded', function() {
    // Embedded translations to avoid CORS issues when opening files directly
    const translations = {
        en: {
            "pageTitle": "CrewOps - Efficient Management of Field Teams and Machinery",
            "pageTitleProject": "Application Details - CrewOps",
            "pageTitleTech": "Technology Stack - CrewOps",
            "pageTitleAbout": "About Me - CrewOps",
            "nav": {
                "home": "Home",
                "project": "Application Details",
                "tech": "Tech Stack",
                "about": "About Me",
                "privacy": "Privacy Policy"
            },
            "hero": {
                "title": "Streamline Field Operations with CrewOps",
                "description": "A comprehensive solution for managing technical crews, monitoring machinery, and tracking maintenance - all in one integrated platform built with modern Java technologies.",
                "tryLive": "Try CrewOps Live",
                "learnMore": "Learn More"
            },
            "features": {
                "title": "Key Features",
                "subtitle": "CrewOps provides everything you need to efficiently manage field teams and machinery operations",
                "teamManagement": {
                    "title": "Team Management",
                    "description": "Assign workers to specific shifts, teams, or machines with real-time tracking of staff availability and role-based access control."
                },
                "machineMonitoring": {
                    "title": "Machine Monitoring",
                    "description": "Comprehensive overview of equipment status (available, assigned, out-of-service) with real-time inventory management and maintenance tracking."
                },
                "faultReporting": {
                    "title": "Fault Reporting",
                    "description": "Direct machine fault reporting with complete logging, status tracking, repair history, and automated notifications to relevant personnel."
                },
                "adminPanel": {
                    "title": "Admin Panel",
                    "description": "Full administrative control with granular user permissions, customizable system views, and comprehensive activity logs for audit and compliance."
                }
            },
            "tech": {
                "title": "Built With Modern Technologies",
                "subtitle": "Leveraging the latest Java ecosystem and cloud technologies",
                "viewFull": "View Full Tech Stack",
                "architectureTitle": "System Architecture",
                "multiModuleTitle": "Multi-Module Gradle Project",
                "multiModuleDesc1": "CrewOps is built with a clean, modular architecture that ensures separation of concerns and maintainability. The project is structured into distinct modules with clear responsibilities, promoting code reusability and maintainability.",
                "multiModuleDesc2": "This modular approach allows for independent development and testing of different components while maintaining a cohesive system architecture. Each module has a specific purpose and well-defined boundaries.",
                "backendModule": "Backend Module",
                "backendItem1": "Spring Boot framework",
                "backendItem2": "PostgreSQL with JPA/Hibernate",
                "backendItem3": "Liquibase for database migrations",
                "backendItem4": "Multi-tenancy with schema-per-tenant",
                "backendItem5": "JWT-based authentication",
                "backendItem6": "Stateless API design",
                "frontendModule": "Frontend Module",
                "frontendItem1": "Spring Boot with Vaadin",
                "frontendItem2": "Modern UI components",
                "frontendItem3": "Integrated Spring Security",
                "frontendItem4": "Vaadin session management",
                "frontendItem5": "Web-based interface",
                "frontendItem6": "Mobile version in development",
                "modelModule": "Model Module",
                "modelItem1": "Shared DTOs and enums",
                "modelItem2": "Domain objects",
                "modelItem3": "Consistent data structures",
                "modelItem4": "Type safety between modules",
                "modelItem5": "HTTP communication contracts",
                "modelItem6": "Validation rules",
                "securityModule": "Security Module",
                "securityItem1": "Shared security interfaces",
                "securityItem2": "JWT token validation",
                "securityItem3": "Role verification",
                "securityItem4": "Consistent auth across modules",
                "securityItem5": "Permission checking",
                "securityItem6": "Security context handling",
                "coreTitle": "Core Technologies",
                "javaDesc": "Core programming language with modern features and strong type safety",
                "springDesc": "Framework for building stand-alone, production-grade applications",
                "postgresDesc": "Robust relational database with advanced features",
                "jpaDesc": "Object-relational mapping for database interactions",
                "liquibaseDesc": "Database schema migration and version control",
                "springSecurityDesc": "Comprehensive security framework for authentication and authorization",
                "jwtDesc": "JSON Web Tokens for stateless authentication",
                "vaadinDesc": "Modern web framework for building UIs in Java",
                "awsDesc": "Cloud computing platform for deployment and hosting",
                "nginxDesc": "High-performance web server and reverse proxy",
                "dockerDesc": "Containerization platform for consistent environments",
                "gradleDesc": "Build automation tool for dependency management",
                "devopsTitle": "DevOps & Deployment",
                "pipelineTitle": "CI/CD Pipeline & Infrastructure",
                "pipelineDesc": "CrewOps implements a modern DevOps approach with automated deployment and cloud infrastructure management. The entire deployment pipeline is automated from code commit to production deployment.",
                "githubTitle": "GitHub Integration",
                "githubItem1": "Repository management",
                "githubItem2": "Issues for planning",
                "githubItem3": "Projects as backlog",
                "githubItem4": "Automated deployment triggers",
                "deploymentTitle": "Deployment Pipeline",
                "deploymentItem1": "Automated testing",
                "deploymentItem2": "Build on merge to main",
                "deploymentItem3": "AWS EC2 deployment",
                "deploymentItem4": "Zero-downtime updates",
                "securityTitle2": "Security & SSL",
                "securityItem7": "Nginx reverse proxy",
                "securityItem8": "HTTPS termination",
                "securityItem9": "SSL certificate management",
                "securityItem10": "Security headers configuration",
                "monitoringTitle": "Monitoring",
                "monitoringItem1": "Application logging",
                "monitoringItem2": "Performance monitoring",
                "monitoringItem3": "Error tracking",
                "monitoringItem4": "Resource utilization",
                "challengesTitle": "Technical Challenges & Solutions",
                "challenge1Title": "Multi-Tenancy Implementation",
                "challenge1Desc": "Implemented schema-per-tenant approach for data isolation in PostgreSQL. Each tenant gets their own database schema while sharing the same application instance. Dynamic schema resolution based on JWT tenant identification ensures proper data separation and security.",
                "challenge2Title": "Cross-Module Consistency",
                "challenge2Desc": "Maintaining consistency between backend and frontend modules through shared model and security modules. The shared modules ensure type safety, consistent data structures, and uniform security validation across all components of the application.",
                "challenge3Title": "Stateless Authentication",
                "challenge3Desc": "Implemented JWT-based authentication with Spring Security for a stateless API design. Token refresh mechanism and role-based authorization at endpoint level provide secure access control without server-side session storage.",
                "challenge4Title": "Real-time Notifications",
                "challenge4Desc": "Designed and implemented a notification system that sends alerts to relevant personnel when machine faults are reported. The system ensures timely communication and quick response to critical operational issues."
            },
            "cta": {
                "title": "Ready to Transform Your Field Operations?",
                "description": "Experience the power of CrewOps and see how it can streamline your team management and machinery monitoring processes.",
                "launch": "Launch CrewOps",
                "github": "View on GitHub"
            },
            "project": {
                "title": "Project Details",
                "subtitle": "Deep dive into CrewOps - a comprehensive field team and machinery management system",
                "overviewTitle": "Project Overview",
                "whatIsTitle": "What is CrewOps?",
                "whatIsDesc1": "CrewOps is a comprehensive field team and machinery management system designed to streamline daily operations for technical crews. The application enables real-time team management, equipment monitoring, and fault reporting - all in one integrated platform.",
                "whatIsDesc2": "Built with a focus on efficiency and scalability, CrewOps helps companies manage their entire workforce and machinery inventory from a single dashboard. The system is designed to handle complex scheduling, maintenance tracking, and operational reporting with ease.",
                "whatIsDesc3": "Currently available as a web application with mobile version in development, CrewOps provides a modern, intuitive interface that makes field operations management simple and effective.",
                "featuresTitle": "Key Features in Detail",
                "teamManagementDetail": "Comprehensive workforce management system that allows administrators to assign workers to specific shifts, teams, or machines. Features include real-time tracking of staff availability, skill-based assignments, and role-based access control. The system ensures optimal resource allocation and reduces scheduling conflicts.",
                "machineMonitoringDetail": "Complete equipment lifecycle management with real-time status tracking. Monitor machinery availability, assignment status, and maintenance schedules. The system provides comprehensive inventory management with automated alerts for maintenance due dates and equipment availability changes.",
                "faultReportingDetail": "Streamlined fault reporting system that allows users to report machine issues directly through the application. Each report includes detailed logging, status tracking, and complete repair history. Automated notifications ensure that relevant personnel are immediately informed when faults are reported.",
                "adminPanelDetail": "Powerful administrative interface with granular user permissions and customizable system views. Administrators have full control over user access, system configuration, and can monitor all activities through comprehensive audit logs. The panel ensures security and compliance with organizational policies.",
                "mvpTitle": "MVP Vision",
                "mvpSubtitle": "Our Minimum Viable Product Goals",
                "mvpDesc": "The MVP version of CrewOps aims to provide a complete solution for managing entire company crews and machinery inventory, with automated fault reporting and notification system to relevant personnel.",
                "mvpGoal1Title": "Complete Crew Management",
                "mvpGoal1Desc": "Manage all company crews, shifts, and assignments in one system",
                "mvpGoal2Title": "Full Machinery Inventory",
                "mvpGoal2Desc": "Track all company machines, their status, and maintenance history",
                "mvpGoal3Title": "Automated Notifications",
                "mvpGoal3Desc": "Instant alerts to relevant personnel when machines report faults",
                "mvpGoal4Title": "Operational Insights",
                "mvpGoal4Desc": "Basic reporting and analytics for operational efficiency",
                "statusTitle": "Current Status",
                "progressTitle": "Development Progress",
                "progressDesc1": "CrewOps is currently in active development with the web version fully functional and the mobile version under development. The core features for team management, machine monitoring, and fault reporting are implemented and being tested with real-world scenarios.",
                "progressDesc2": "The system is deployed on AWS EC2 with HTTPS configuration and automated CI/CD pipeline. Regular updates are being pushed based on user feedback and evolving requirements."
            },
            "about": {
                "title": "About Me",
                "subtitle": "Java developer passionate about building enterprise applications and solving real-world problems",
                "intro1": "I'm a self-taught Java developer with two years of dedicated learning and practical application. My journey in software development began with a fascination for solving real-world problems through technology, leading me to specialize in enterprise Java applications.",
                "intro2": "I believe in learning by building. CrewOps represents not just a project, but a comprehensive exploration of modern Java development practices, from database design to deployment automation. My approach combines theoretical knowledge with hands-on implementation to create robust, scalable solutions.",
                "intro3": "Apart from developing the CrewOps project, I try to participate in group projects, which gives me the opportunity to meet new people and explore technological solutions.",
                "skillsTitle": "Technical Skills",
                "skillsSubtitle": "My Development Journey",
                "skillsDesc": "As a self-taught developer, I've focused on building practical skills through hands-on projects.",
                "coreJava": "Core Java",
                "skillOOP": "OOP Principles",
                "skillCollections": "Collections Framework",
                "skillStreams": "Streams API",
                "skillExceptions": "Exception Handling",
                "skillJava17": "Java 17+ Features",
                "springEcosystem": "Spring Ecosystem",
                "skillSpringBoot": "Spring Boot",
                "skillSpringMVC": "Spring MVC",
                "skillSpringSecurity": "Spring Security",
                "skillSpringData": "Spring Data JPA",
                "skillRestAPIs": "REST APIs",
                "skillDependencyInjection": "Dependency Injection",
                "database": "Database & ORM",
                "skillPostgreSQL": "PostgreSQL",
                "skillJPA": "JPA/Hibernate",
                "skillBasicDB": "Basic Database Concepts",
                "skillLiquibase": "Liquibase",
                "security": "Security",
                "skillJWT": "JWT Authentication",
                "skillRoleBased": "Role-Based Access",
                "skillMultitenancy": "Multi-tenancy",
                "frontend": "Frontend",
                "skillVaadin": "Vaadin Framework",
                "skillUI": "UI Components",
                "devOps": "DevOps & Cloud",
                "skillAWS": "AWS EC2",
                "skillDocker": "Docker",
                "skillNginx": "Nginx",
                "skillCICD": "CI/CD Basics",
                "skillGit": "Git",
                "journeyTitle": "My Learning Journey",
                "journeySubtitle": "From Beginner to Java Developer",
                "journeyDesc": "My journey into software development started with curiosity and determination. Here's a timeline of my learning path and key milestones:",
                "year2023Start": "2023 - Getting Started",
                "journey2023Start": "Discovered programming and began learning Java fundamentals. Completed online courses and built simple console applications to understand core concepts.",
                "year2023Projects": "2023 - First Projects",
                "journey2023Projects": "Created basic Java applications to practice programming concepts. Focused on understanding object-oriented programming principles and basic design patterns.",
                "year2024Web": "2024 - Web Development",
                "journey2024Web": "Explored web development with Spring Boot. Built REST APIs and learned about database integration with JPA/Hibernate.",
                "year2025CrewOps": "2025 - CrewOps Begins",
                "journey2025CrewOps": "Started working on CrewOps as a comprehensive project to apply and expand my skills. Implemented multi-module architecture and advanced features.",
                "year2025DevOps": "2025 - DevOps & Deployment",
                "journey2025DevOps": "Learned about cloud deployment, CI/CD pipelines, and containerization. Successfully deployed CrewOps to AWS with automated deployment.",
                "present": "Present - Continuous Learning",
                "journeyPresent": "Currently exploring microservices architecture, advanced security patterns, and performance optimization techniques.",
                "contactTitle": "Let's Connect",
                "getInTouch": "Get In Touch",
                "contactDesc": "I'm actively seeking opportunities to contribute to Java projects, collaborate with other developers, and take on roles where I can apply and expand my skills. Feel free to reach out!",
                "sendEmail": "Send Email",
                "linkedin": "LinkedIn"
            },
            "privacy": {
                "pageTitle": "Privacy Policy — CrewOps",
                "logo": "CrewOps",
                "tagline": "Efficient Management of Field Teams and Machinery",
                "hero": {
                    "title": "Privacy Policy",
                    "desc": "Document outlining the principles of personal data processing in the CrewOps Service."
                },
                "meta": {
                    "effectiveDateLabel": "Effective Date:",
                    "effectiveDate": "October 1, 2025"
                },
                "toc": {
                    "definitions": "Definitions",
                    "section1": "1. Personal Data Controller",
                    "section2": "2. Scope of Collected Data",
                    "section2a": "2.a Technical Data and Cookies",
                    "section3": "3. Purposes of Data Processing",
                    "section4": "4. Legal Basis",
                    "section5": "5. Data Retention Period",
                    "section6": "6. Rights of Users and Clients",
                    "section6a": "6.a Deletion of Client Account",
                    "section7": "7. Contact",
                    "section8": "8. Policy Update",
                    "section9": "9. Document Preparation Information"
                },
                "definitions": {
                    "title": "Definitions",
                    "intro": "For the purposes of this Privacy Policy, the following meanings of terms are adopted:",
                    "service": "<strong>Service</strong> – a web application provided by Janusz Kuźnik at https://crewops.devsmith.eu, enabling the provision of SaaS services.",
                    "admin": "<strong>Service Administrator</strong> – Janusz Kuźnik, operating as a sole proprietor; under the GDPR, serves as:",
                    "admin.dataController": "<strong>personal data controller for Clients</strong> (e.g., contact details of persons registering a company account),",
                    "admin.processor": "<strong>data processor</strong> for personal data of Users that have been entrusted by the Client.",
                    "client": "<strong>Client</strong> – an entity (individual, company or other organizational unit) using the Service. The Client acts as <strong>personal data controller for its Users</strong>.",
                    "user": "<strong>User</strong> – a natural person whose personal data has been entered into the Service by the Client (e.g., Client's employee, collaborator or contractor)."
                },
                "section1": {
                    "title": "1. Personal Data Controller",
                    "adminIntro": "The personal data controller of Clients using the Service is the Service Administrator - <strong>Janusz Kuźnik</strong>.",
                    "contact": "Contact with the Service Administrator is possible via e-mail: <strong>janusz.kuznik@devsmith.eu</strong>",
                    "privacyNote": "The Service Administrator does not disclose his residential address or other contact details in this document due to privacy protection. This data may be provided to the Client or relevant authorities only upon their justified request, in accordance with applicable law.",
                    "processorNote": "Personal data of Users in the Service are processed by the Service Administrator only on behalf of the Client, who is the personal data controller of its employees, collaborators and contractors. The Service Administrator in this capacity acts as <strong>data processor</strong> based on a personal data processing agreement. The Client may process User data <strong>only to the extent and in the manner made available by the Service</strong>; any other actions are prohibited."
                },
                "section2": {
                    "title": "2. Scope of Collected Data",
                    "clientAccount": {
                        "title": "Data regarding the Client's (company) account:",
                        "companyName": "company name,",
                        "nip": "tax identification number,",
                        "address": "company headquarters address,",
                        "email": "e-mail address indicated for contact and registration.",
                        "note": "The above data concerns business entities and as a rule does not constitute personal data under the GDPR, except when the e-mail address allows identification of a natural person."
                    },
                    "userAccounts": {
                        "title": "Data regarding User accounts:",
                        "name": "first and last name,",
                        "employeeId": "employee identifier (e.g., employee number) assigned by the Client or automatically generated,",
                        "optional": "optionally: e-mail address and phone number (usually business), if assigning this data enables the use of additional Service functionalities, such as notifications.",
                        "processorNote": "The Administrator indicates that he is not the direct controller of Users' personal data, but processes them solely based on a <strong>personal data processing agreement</strong> concluded with the Client, who acts as the data controller of its employees, collaborators and contractors.",
                        "purpose": "User data is processed to the extent necessary to provide services offered by the Service and to ensure the functionality of assigned User accounts, including enabling the use of assigned functions and notifications."
                    }
                },
                "section2a": {
                    "title": "2.a Technical Data and Cookies",
                    "intro": "When using the Service, technical data regarding the User's device may be processed, including, among others, internet browser type, operating system, language settings and other data automatically transmitted as part of standard network communication with the Service.",
                    "cookies": "The Service may use <strong>cookies necessary for the proper functioning of the system and ensuring User session security</strong>. The Administrator does not use tracking mechanisms or process data for profiling or delivering personalized content.",
                    "ip": "In case of future implementation of login functionality, the Service may also process <strong>User's IP address</strong> to ensure security, monitor abuse and diagnose technical problems.",
                    "purposeIntro": "Technical data and cookie information are processed only to the extent necessary for:",
                    "purpose": {
                        "1": "ensuring proper functioning of the Service,",
                        "2": "maintaining system security,",
                        "3": "diagnosing and resolving technical problems."
                    }
                },
                "section3": {
                    "title": "3. Purposes of Personal Data Processing",
                    "intro": "Users' personal data are processed in the Service only to the extent and for purposes necessary to provide services offered by the Service, in particular:",
                    "serviceManagement": {
                        "title": "Service Implementation",
                        "clientUsers": "enabling the Client (company) to manage its own users,",
                        "userFeatures": "enabling Users to use functionalities assigned to their accounts, including notifications and access to Service content."
                    },
                    "accountManagement": {
                        "title": "Account Management and Authorization",
                        "integrity": "maintaining correctness and integrity of Client and User accounts,",
                        "security": "in case of login implementation: ensuring access security and authorization."
                    },
                    "security": {
                        "title": "Security and Technical Diagnostics",
                        "systemSafety": "ensuring Service and user session security,",
                        "diagnostics": "monitoring system operation correctness, diagnosing and resolving technical problems,",
                        "misuse": "potential monitoring of abuses within the system (including IP addresses in the future)."
                    },
                    "clientSupport": {
                        "title": "Communication with Client and Technical Support",
                        "contact": "enabling contact with the Client regarding administrative, technical or service implementation matters, including via the e-mail address provided when registering a company account,",
                        "support": "providing technical assistance and support in using the Service based on Client or User data."
                    }
                },
                "section4": {
                    "title": "4. Legal Basis for Personal Data Processing",
                    "intro": "Processing of Users' personal data in the Service is carried out under the GDPR, in particular:",
                    "art6b": {
                        "title": "Art. 6(1)(b) GDPR – necessity for contract performance",
                        "desc": "User data is processed to the extent necessary to provide the Service to the Client (company) and to implement functionalities assigned to user accounts."
                    },
                    "art6f": {
                        "title": "Art. 6(1)(f) GDPR – legitimate interest of the controller or processor",
                        "desc": "Technical data, including information about devices, sessions and potential IP addresses, are processed to ensure Service security, monitor system operation correctness, diagnose technical problems and prevent abuse. The legitimate interest also includes the possibility of providing technical assistance to Users."
                    },
                    "art6c": {
                        "title": "Art. 6(1)(c) GDPR – legal obligation",
                        "desc": "To the extent that the Service is required to fulfill legal obligations (e.g., storing data necessary for Client's tax or accounting purposes), processing is carried out to fulfill these obligations."
                    }
                },
                "section5": {
                    "title": "5. Data Retention Period",
                    "clientData": "Client data (company) is stored for the entire duration of service provision by the Service. In case of termination of service use by the Client, Client data and Users directly assigned to that Client are additionally stored for a period of up to <strong>6 months</strong> to enable potential reuse of the Service and preserve activity history. After this period, Client and User data are <strong>permanently and irreversibly deleted or anonymized</strong>, and their recovery is not possible.",
                    "userData": "User data is stored <strong>as long as the Client uses the Service</strong>. This means that in case of termination of cooperation between the User and the Client (e.g., termination of employment, end of contractual cooperation), the User may lose access to the system, but <strong>history of their activity in the Service remains preserved for the Client's needs</strong>, in accordance with their business requirements.",
                    "technicalLogs": "Technical data and system logs are stored only to the extent necessary to ensure Service security and diagnose and resolve technical problems, for a period not longer than necessary to achieve these purposes."
                },
                "section6": {
                    "title": "6. Rights of Users and Clients",
                    "intro": "Users have the right to:",
                    "access": "<strong>Data access</strong> – the right to request from the controller (Client) information about processed personal data concerning the User, including purpose, scope and retention period.",
                    "correction": "<strong>Data correction</strong> – the right to request from the controller (Client) correction or completion of incorrect or incomplete personal data.",
                    "deletion": {
                        "title": "<strong>Data deletion ('right to be forgotten')</strong> – the right to request from the controller (Client) deletion of personal data, provided there are no other justified grounds for their retention.",
                        "note": "Depending on the Client's business needs, the deletion operation may be implemented in one of two variants:",
                        "permanent": "<strong>Irreversible deletion of the User account along with their entire activity history</strong> in the Service.",
                        "anonymization": "<strong>Anonymization of the User account</strong>, as a result of which personal data are no longer associated with a specific person, allowing to preserve activity history in the system, in accordance with the Client's requirements.",
                        "processorNote": "The Service Administrator implements the above operations solely based on the Client's request, who is the personal data controller of Users."
                    },
                    "limitation": "<strong>Limitation of processing</strong> – the right to request from the controller (Client) restriction of processing in cases provided for by GDPR provisions.",
                    "objection": "<strong>Objection to data processing</strong> – the right to object to the processing of personal data in cases provided for by GDPR provisions.",
                    "complaint": "<strong>Complaint to the supervisory authority</strong> – the right to lodge a complaint with the President of the Personal Data Protection Office if it is considered that the processing of personal data violates the provisions of the GDPR."
                },
                "section6a": {
                    "title": "6.a Deletion of Client (company) Account",
                    "clientRequest": "The Client has the right to request deletion of their account in the Service.",
                    "irreversible": "Deletion of the Client account is <strong>irreversible</strong> and results in immediate deletion of:",
                    "userAccounts": "accounts of all Users assigned to that Client (employees, collaborators, contractors),",
                    "history": "entire history of data related to the Client account and its Users."
                },
                "section7": {
                    "title": "7. Contact regarding Personal Data Protection",
                    "intro": "All questions, applications or requests regarding personal data processing in connection with the use of the Service may be directed to the Service Administrator via electronic mail:",
                    "email": "crewops@devsmith.eu",
                    "responseTime": "The Service Administrator considers requests immediately, taking into account the deadlines provided for in Art. 12 GDPR."
                },
                "section8": {
                    "title": "8. Privacy Policy Update",
                    "updateRight": "The Service Administrator reserves the right to <strong>update this Privacy Policy</strong> at any time, in particular in case of changes in Service functionality, legal requirements or data processing practices.",
                    "notification": "The System Administrator <strong>declares that it will inform Service Clients about planned changes to the Privacy Policy</strong> with appropriate advance notice to enable them to familiarize themselves with the new provisions.",
                    "effective": "Policy updates become effective upon their publication in the Service at the same URL address. Regular review of the Policy content is recommended for current information on personal data processing methods."
                },
                "section9": {
                    "title": "9. Document Preparation Information",
                    "intro": "The Privacy Policy document was prepared by the <strong>Service Administrator</strong> to transparently present the principles of personal data processing in the Service.",
                    "effectiveDate": "Effective date: <strong>October 1, 2025</strong>."
                },
                "footer": {
                    "copyright": "© 2025 CrewOps. All rights reserved.",
                    "author": "Design and implementation: Janusz Kuźnik"
                }
            },
            "footer": {
                "copyright": "© 2025 CrewOps. All rights reserved.",
                "designedBy": "Designed and built by Janusz Kuźnik"
            }
        },
            pl: {
                "pageTitle": "CrewOps - Efektywne Zarządzanie Zespołami Terenowymi i Maszynami",
                "pageTitleProject": "Aplikacja w szczegółach - CrewOps",
                "pageTitleTech": "Stos Technologiczny - CrewOps",
                "pageTitleAbout": "O Mnie - CrewOps",
                "nav": {
                    "home": "Strona Główna",
                    "project": "Aplikacja w szczegółach",
                    "tech": "Stos Technologiczny",
                    "about": "O Mnie",
                    "privacy": "Polityka Prywatności"
                },
                "hero": {
                    "title": "Usprawnij Zarządzanie Zespołem z CrewOps",
                    "description": "Kompleksowe rozwiązanie do zarządzania zespołami technicznymi, monitorowania maszyn i śledzenia konserwacji - wszystko w jednej zintegrowanej platformie zbudowanej przy użyciu nowoczesnych technologii Java.",
                    "tryLive": "Wypróbuj CrewOps",
                    "learnMore": "Dowiedz się więcej"
                },
                "features": {
                    "title": "Kluczowe Funkcje",
                    "subtitle": "CrewOps zapewnia wszystko, czego potrzebujesz do efektywnego zarządzania zespołami terenowymi i dostępnym parkiem maszyn",
                    "teamManagement": {
                        "title": "Zarządzanie Zespołem",
                        "description": "Przypisz pracowników do konkretnych zmian, zespołów lub maszyn z śledzeniem dostępności personelu w czasie rzeczywistym i kontrolą dostępu opartą na pełnionej funkcji."
                    },
                    "machineMonitoring": {
                        "title": "Monitorowanie Maszyn",
                        "description": "Kompleksowy przegląd statusu sprzętu (dostępnego, przypisanego do pracownika, wyłączonego z użytku) w czasie rzeczywistym."
                    },
                    "faultReporting": {
                        "title": "Zgłaszanie Usterki",
                        "description": "Bezpośrednie zgłaszanie usterek maszyn z pełnym rejestrowaniem, śledzeniem statusu, historią napraw i automatycznymi powiadomieniami dla odpowiednich osób."
                    },
                    "adminPanel": {
                        "title": "Panel Administracyjny",
                        "description": "Pełna kontrola administracyjna ze szczegółowymi uprawnieniami użytkowników, konfigurowalnymi widokami systemu i kompleksowymi logami działań do audytu i zgodności."
                    }
                },
                "tech": {
                    "title": "Zbudowany z Wykorzystaniem Nowoczesnych Technologii",
                    "subtitle": "Wykorzystując najnowszy ekosystem Javy i technologie chmurowe",
                    "viewFull": "Zobacz Pełny Stos Technologiczny",
                    "architectureTitle": "Architektura Systemu",
                    "multiModuleTitle": "Wielomodułowy Projekt Gradle",
                    "multiModuleDesc1": "CrewOps jest zbudowany z czystą, modułową architekturą, która zapewnia separację odpowiedzialności i łatwość utrzymania. Projekt jest zorganizowany w odrębne moduły o jasnych odpowiedzialnościach, promując ponowne wykorzystanie kodu i łatwość utrzymania.",
                    "multiModuleDesc2": "To podejście modułowe pozwala na niezależny rozwój i testowanie różnych komponentów, przy jednoczesnym zachowaniu spójnej architektury systemu. Każdy moduł ma określony cel i dobrze zdefiniowane granice.",
                    "backendModule": "Moduł Backend",
                    "backendItem1": "Framework Spring Boot",
                    "backendItem2": "PostgreSQL z JPA/Hibernate",
                    "backendItem3": "Liquibase do migracji bazy danych",
                    "backendItem4": "Wielodostępność z podejściem schema-per-tenant",
                    "backendItem5": "Uwierzytelnianie oparte na JWT",
                    "backendItem6": "Projektowanie bezstanowego API",
                    "frontendModule": "Moduł Frontend",
                    "frontendItem1": "Spring Boot z Vaadin",
                    "frontendItem2": "Nowoczesne komponenty UI",
                    "frontendItem3": "Zintegrowane Spring Security",
                    "frontendItem4": "Zarządzanie sesją Vaadin",
                    "frontendItem5": "Interfejs webowy",
                    "frontendItem6": "Wersja mobilna w rozwoju",
                    "modelModule": "Moduł Model",
                    "modelItem1": "Współdzielone DTO i enumy",
                    "modelItem2": "Obiekty domenowe",
                    "modelItem3": "Spójne struktury danych",
                    "modelItem4": "Bezpieczeństwo typów między modułami",
                    "modelItem5": "Kontrakty komunikacji HTTP",
                    "modelItem6": "Reguły walidacji",
                    "securityModule": "Moduł Bezpieczeństwa",
                    "securityItem1": "Współdzielone interfejsy bezpieczeństwa",
                    "securityItem2": "Walidacja tokenów JWT",
                    "securityItem3": "Weryfikacja ról",
                    "securityItem4": "Spójne uwierzytelnianie między modułami",
                    "securityItem5": "Sprawdzanie uprawnień",
                    "securityItem6": "Obsługa kontekstu bezpieczeństwa",
                    "coreTitle": "Główne Technologie",
                    "javaDesc": "Podstawowy język programowania z nowoczesnymi funkcjami i silną typizacją",
                    "springDesc": "Framework do budowania samodzielnych, produkcyjnych aplikacji",
                    "postgresDesc": "Solidna relacyjna baza danych z zaawansowanymi funkcjami",
                    "jpaDesc": "Mapowanie obiektowo-relacyjne do interakcji z bazą danych",
                    "liquibaseDesc": "Migracja schematu bazy danych i kontrola wersji",
                    "springSecurityDesc": "Kompleksowy framework bezpieczeństwa do uwierzytelniania i autoryzacji",
                    "jwtDesc": "Tokeny Web JSON do bezstanowego uwierzytelniania",
                    "vaadinDesc": "Nowoczesny framework web do budowy interfejsów użytkownika w Javie",
                    "awsDesc": "Platforma przetwarzania w chmurze do wdrożenia i hostingu",
                    "nginxDesc": "Wysokowydajny serwer WWW i reverse proxy",
                    "dockerDesc": "Platforma kontenerów do spójnych środowisk",
                    "gradleDesc": "Narzędzie automatyzacji budowania do zarządzania zależnościami",
                    "devopsTitle": "DevOps i Wdrożenie",
                    "pipelineTitle": "Potok CI/CD i Infrastruktura",
                    "pipelineDesc": "CrewOps implementuje nowoczesne podejście DevOps z automatycznym wdrożeniem i zarządzaniem infrastrukturą chmurową. Cały potok wdrożeniowy jest zautomatyzowany od zatwierdzenia kodu do wdrożenia produkcyjnego.",
                    "githubTitle": "Integracja z GitHub",
                    "githubItem1": "Zarządzanie repozytorium",
                    "githubItem2": "Zgłoszenia do planowania",
                    "githubItem3": "Projekty jako backlog",
                    "githubItem4": "Automatyczne wyzwalacze wdrożeń",
                    "deploymentTitle": "Potok Wdrożeniowy",
                    "deploymentItem1": "Automatyczne testowanie",
                    "deploymentItem2": "Budowanie przy scalaniu do głównej gałęzi",
                    "deploymentItem3": "Wdrożenie na AWS EC2",
                    "deploymentItem4": "Aktualizacje bez przestojów",
                    "securityTitle2": "Bezpieczeństwo i SSL",
                    "securityItem7": "Reverse proxy Nginx",
                    "securityItem8": "Terminacja HTTPS",
                    "securityItem9": "Zarządzanie certyfikatami SSL",
                    "securityItem10": "Konfiguracja nagłówków bezpieczeństwa",
                    "monitoringTitle": "Monitorowanie",
                    "monitoringItem1": "Logowanie aplikacji",
                    "monitoringItem2": "Monitorowanie wydajności",
                    "monitoringItem3": "Śledzenie błędów",
                    "monitoringItem4": "Wykorzystanie zasobów",
                    "challengesTitle": "Wyzwania Techniczne i Rozwiązania",
                    "challenge1Title": "Implementacja Wielodostępności",
                    "challenge1Desc": "Zaimplementowano podejście schema-per-tenant do izolacji danych w PostgreSQL. Każdy najemca otrzymuje własny schemat bazy danych, współdzieląc tę samą instancję aplikacji. Dynamiczne rozpoznawanie schematu oparte na identyfikatorze najemcy JWT zapewnia właściwą separację danych i bezpieczeństwo.",
                    "challenge2Title": "Spójność Między Modułami",
                    "challenge2Desc": "Utrzymywanie spójności między modułami backend i frontend poprzez współdzielone moduły modelu i bezpieczeństwa. Współdzielone moduły zapewniają bezpieczeństwo typów, spójne struktury danych i jednolitą walidację bezpieczeństwa we wszystkich komponentach aplikacji.",
                    "challenge3Title": "Bezstanowe Uwierzytelnianie",
                    "challenge3Desc": "Zaimplementowano uwierzytelnianie oparte na JWT z Spring Security do projektowania bezstanowego API. Mechanizm odświeżania tokenów i autoryzacja oparta na rolach na poziomie punktów końcowych zapewniają bezpieczną kontrolę dostępu bez przechowywania sesji po stronie serwera.",
                    "challenge4Title": "Powiadomienia w Czasie Rzeczywistym",
                    "challenge4Desc": "Zaprojektowano i zaimplementowano system powiadomień, który wysyła alerty do odpowiednich osób, gdy zgłaszane są usterki maszyn. System zapewnia terminową komunikację i szybką reakcję na krytyczne problemy operacyjne."
                },
                "cta": {
                    "title": "Gotowy na Transformację Swoich Operacji Terenowych?",
                    "description": "Doświadcz mocy CrewOps i zobacz, jak może usprawnić zarządzanie zespołami i monitorowanie maszyn.",
                    "launch": "Uruchom CrewOps",
                    "github": "Zobacz na GitHub"
                },
                "project": {
                    "title": "Szczegóły Projektu",
                    "subtitle": "Głębsze spojrzenie na CrewOps - kompleksowy system zarządzania zespołami pracowników i maszynami",
                    "overviewTitle": "Przegląd Projektu",
                    "whatIsTitle": "Czym jest CrewOps?",
                    "whatIsDesc1": "CrewOps to kompleksowy system zarządzania zespołami terenowymi i maszynami zaprojektowany w celu usprawnienia codziennych operacji zespołów technicznych. Aplikacja umożliwia zarządzanie zespołami w czasie rzeczywistym, monitorowanie sprzętu i zgłaszanie usterek - wszystko w jednej zintegrowanej platformie.",
                    "whatIsDesc2": "Zbudowany z naciskiem na wydajność i skalowalność, CrewOps pomaga firmom zarządzać całą siłą roboczą i zapasami maszyn z jednego panelu. System jest zaprojektowany do obsługi złożonego harmonogramowania, śledzenia historii awarii i napraw oraz prac serwisowych maszyn.",
                    "whatIsDesc3": "Obecnie dostępny jako aplikacja webowa z wersją mobilną w rozwoju, CrewOps zapewnia nowoczesny, intuicyjny interfejs, który sprawia, że zarządzanie operacjami terenowymi jest proste i skuteczne.",
                    "featuresTitle": "Kluczowe Funkcje w Szczegółach",
                    "teamManagementDetail": "Kompleksowy system zarządzania siłą roboczą, który pozwala administratorom przypisywać pracowników do konkretnych zmian, zespołów lub maszyn. Funkcje obejmują śledzenie dostępności personelu w czasie rzeczywistym, przydziały oparte na umiejętnościach oraz kwalifikacjach. System zapewnia optymalne alokowanie zasobów i redukuje konflikty harmonogramu. Jedną z dodatkowych funkcji jest odpowiednio wczesne powiadamianie o zbliżających się terminach ważności kwalifikacji lub badań medycznych danego pracownika.",
                    "machineMonitoringDetail": "Kompletne zarządzanie cyklem życia sprzętu ze śledzeniem statusu w czasie rzeczywistym. Monitoruj dostępność maszyn, status przypisania i harmonogramy konserwacji. System zapewnia kompleksowe zarządzanie zapasami z automatycznymi alertami o nadchodzących terminach konserwacji i zmianach dostępności sprzętu.",
                    "faultReportingDetail": "Usprawniony system zgłaszania usterek, który pozwala użytkownikom zgłaszać problemy z maszynami bezpośrednio przez aplikację. Każde zgłoszenie jest objęte szczegółowym rejestrowaniem oraz umożliwia śledzenie statusu i pełną historię napraw. Automatyczne powiadomienia zapewniają, że odpowiednie osoby są natychmiast informowane o zgłoszonych usterkach.",
                    "adminPanelDetail": "Interfejs administracyjny ze szczegółowymi uprawnieniami użytkowników i konfigurowalnymi widokami systemu. Administratorzy mają kontrolę nad dostępem użytkowników, konfiguracją systemu i mogą monitorować wszystkie działania za pomocą kompleksowych logów audytowych.",
                    "mvpTitle": "Wizja MVP",
                    "mvpSubtitle": "Nasze Cele Minimalnego Produktu Wdrożeniowego",
                    "mvpDesc": "Wersja MVP CrewOps ma na celu zapewnienie kompleksowego rozwiązania do zarządzania całą kadrą firmy oraz dostępnym parkiem maszyn, z automatycznym zgłaszaniem usterek i systemem powiadamiania odpowiednich osób.",
                    "mvpGoal1Title": "Kompletne Zarządzanie Zespołami",
                    "mvpGoal1Desc": "Zarządzaj wszystkimi zespołami firmy, zmianami i przydziałami w jednym systemie",
                    "mvpGoal2Title": "Pełny Inwentarz Maszyn",
                    "mvpGoal2Desc": "Śledź wszystkie maszyny firmy, ich status i historię konserwacji",
                    "mvpGoal3Title": "Automatyczne Powiadomienia",
                    "mvpGoal3Desc": "Natychmiastowe alerty dla odpowiednich osób, gdy maszyny zgłaszają usterki",
                    "mvpGoal4Title": "Wgląd w Operacje",
                    "mvpGoal4Desc": "Podstawowe raportowanie i analityka dla efektywności operacyjnej",
                    "statusTitle": "Obecny Status",
                    "progressTitle": "Postęp Rozwoju",
                    "progressDesc1": "CrewOps jest obecnie w fazie rozwoju funkcjonalnej wersji webowej z jednoczesnym przygotowaniem wersji mobilnej. Kluczowe funkcje zarządzania zespołami, monitorowania maszyn i zgłaszania usterek są zaimplementowane i testowane w rzeczywistych scenariuszach.",
                    "progressDesc2": "Regularne aktualizacje są wdrażane na podstawie opinii użytkowników i ewoluujących wymagań."
                },
                "about": {
                    "title": "O Mnie",
                    "subtitle": "Programista Java pasjonujący się budowaniem aplikacji enterprise i rozwiązywaniem rzeczywistych problemów",
                    "intro1": "Jestem programistą Javy z dwoma latami dedykowanej nauki i praktycznego zastosowania. Moja podróż w tworzeniu oprogramowania zaczęła się od fascynacji rozwiązywaniem rzeczywistych problemów za pomocą technologii, co prowadziło mnie do specjalizacji w aplikacjach enterprise Java.",
                    "intro2": "CrewOps reprezentuje nie tylko projekt, ale kompleksowe eksplorowanie nowoczesnych praktyk tworzenia oprogramowania w Javie, od projektowania baz danych po automatyzację wdrażenia. Moje podejście łączy wiedzę teoretyczną z praktyczną implementacją w celu tworzenia solidnych, skalowalnych rozwiązań.",
                    "intro3": "Poza rozwojej projektu CrewOps staram się brać udział w grupowych projektach co daje mi możliwość poznawania nowych osób oraz rozwiązań technologicznych.",
                    "skillsTitle": "Umiejętności Techniczne",
                    "skillsSubtitle": "W tym czuję się swobodnie",
                    "skillsDesc": "Jako samouczący się programista, skupiłem się na budowaniu praktycznych umiejętności poprzez projekty praktyczne.",
                    "coreJava": "Podstawy Javy",
                    "skillOOP": "Zasady OOP",
                    "skillCollections": "Framework Collections",
                    "skillStreams": "Streams API",
                    "skillExceptions": "Obsługa Wyjątków",
                    "skillJava17": "Funkcje Javy 17+",
                    "springEcosystem": "Ekosystem Spring",
                    "skillSpringBoot": "Spring Boot",
                    "skillSpringMVC": "Spring MVC",
                    "skillSpringSecurity": "Spring Security",
                    "skillSpringData": "Spring Data JPA",
                    "skillRestAPIs": "API REST",
                    "skillDependencyInjection": "Wstrzykiwanie Zależności",
                    "database": "Baza Danych i ORM",
                    "skillPostgreSQL": "PostgreSQL",
                    "skillJPA": "JPA/Hibernate",
                    "skillBasicDB": "Podstawowe Koncepcje Baz Danych",
                    "skillLiquibase": "Liquibase",
                    "security": "Bezpieczeństwo",
                    "skillJWT": "Uwierzytelnianie JWT",
                    "skillRoleBased": "Dostęp Oparty na Rolach",
                    "skillMultitenancy": "Wielodostępność",
                    "frontend": "Frontend",
                    "skillVaadin": "Framework Vaadin",
                    "skillUI": "Komponenty UI",
                    "devOps": "DevOps i Chmura",
                    "skillAWS": "AWS EC2",
                    "skillDocker": "Docker",
                    "skillNginx": "Nginx",
                    "skillCICD": "Podstawy CI/CD",
                    "skillGit": "Git",
                    "journeyTitle": "Moja Podróż Rozwojowa",
                    "journeySubtitle": "Od Początkującego do Programisty Javy",
                    "journeyDesc": "Moja podróż w tworzeniu oprogramowania zaczęła się z pasji i determinacji. Oto oś czasu mojej ścieżki nauki i kluczowe kamienie milowe:",
                    "year2023Start": "2023 - Początki",
                    "journey2023Start": "Zacząłem uczyć się podstaw Javy. Ukończyłem kurs online i zbudowałem proste aplikacje konsolowe, aby zrozumieć podstawowe koncepcje.",
                    "year2023Projects": "2023 - Pierwsze Projekty",
                    "journey2023Projects": "Stworzyłem podstawowe aplikacje Javy do ćwiczenia koncepcji programowania. Skupiłem się na zrozumieniu zasad programowania obiektowego i podstawowych wzorców projektowych.",
                    "year2024Web": "2024 - Tworzenie Web",
                    "journey2024Web": "Zgłębiłem tworzenie web z Spring Boot. Zbudowałem API REST po czym zacząłem brać udział w projektach grupowych.",
                    "year2025CrewOps": "2025 - Początek CrewOps",
                    "journey2025CrewOps": "Zacząłem pracować nad CrewOps jako kompleksowym projektem do zastosowania i rozwinięcia moich umiejętności. Zaimplementowałem architekturę wielomodułową i zaawansowane funkcje.",
                    "year2025DevOps": "2025 - DevOps i Wdrożenie",
                    "journey2025DevOps": "Pomyślnie wdrożyłem CrewOps korzystająć z rozwiązań AWS z automatycznym procesem CI/CD.",
                    "present": "Obecnie - Ciąg Dalszy Nauki",
                    "journeyPresent": "Obecnie eksploruję sposoby monitorowania aplikacji, zaawansowane wzorce bezpieczeństwa i techniki optymalizacji wydajności.",
                    "contactTitle": "Skontaktuj Się",
                    "getInTouch": "Nawiąż Kontakt",
                    "contactDesc": "Aktywnie szukam możliwości dołączania do projektów Java, współpracy z innymi programistami i podejmowania ról, w których mogę zastosować i rozwinąć moje umiejętności.",
                    "sendEmail": "Wyślij Email",
                    "linkedin": "LinkedIn"
                },
                "privacy": {
                    "pageTitle": "Polityka Prywatności — CrewOps",
                    "logo": "CrewOps",
                    "tagline": "Efektywne Zarządzanie Zespołami Terenowymi i Maszynami",
                    "hero": {
                        "title": "Polityka Prywatności",
                        "desc": "Dokument przedstawia zasady przetwarzania danych osobowych w Serwisie CrewOps."
                    },
                    "meta": {
                        "effectiveDateLabel": "Data wprowadzenia w życie:",
                        "effectiveDate": "1 października 2025 roku"
                    },
                    "toc": {
                        "definitions": "Definicje",
                        "section1": "1. Administrator danych osobowych",
                        "section2": "2. Zakres zbieranych danych",
                        "section2a": "2.a Dane techniczne i pliki cookies",
                        "section3": "3. Cele przetwarzania danych",
                        "section4": "4. Podstawa prawna",
                        "section5": "5. Okres przechowywania danych",
                        "section6": "6. Prawa użytkowników i Klientów",
                        "section6a": "6.a Usunięcie konta Klienta",
                        "section7": "7. Kontakt",
                        "section8": "8. Aktualizacja Polityki",
                        "section9": "9. Informacje o sporządzeniu dokumentu"
                    },
                    "definitions": {
                        "title": "Definicje",
                        "intro": "Na potrzeby niniejszej Polityki Prywatności przyjmuje się następujące znaczenia pojęć:",
                        "service": "<strong>Serwis</strong> – aplikacja internetowa udostępniana przez Janusza Kuźnika pod adresem https://crewops.devsmith.eu, umożliwiająca świadczenie usług SaaS.",
                        "admin": "<strong>Administrator Serwisu</strong> – Janusz Kuźnik, prowadzący działalność jako osoba fizyczna; w rozumieniu RODO pełni rolę:",
                        "admin.dataController": "<strong>administratora danych osobowych Klientów</strong> (np. dane kontaktowe osób rejestrujących konto firmowe),",
                        "admin.processor": "<strong>podmiotu przetwarzającego</strong> wobec danych osobowych Użytkowników, które zostały powierzone przez Klienta.",
                        "client": "<strong>Klient</strong> – podmiot (osoba fizyczna, firma lub inna jednostka organizacyjna) korzystający z usług Serwisu. Klient pełni rolę <strong>administratora danych osobowych swoich Użytkowników</strong>.",
                        "user": "<strong>Użytkownik</strong> – osoba fizyczna, której dane osobowe zostały wprowadzone do Serwisu przez Klienta (np. pracownik Klienta, współpracownik lub kontrahent)."
                    },
                    "section1": {
                        "title": "1. Administrator danych osobowych",
                        "adminIntro": "Administratorem danych osobowych Klientów korzystających z Serwisu jest Administrator Serwisu - <strong>Janusz Kuźnik</strong>.",
                        "contact": "Kontakt z Administratorem Serwisu jest możliwy za pośrednictwem adresu e-mail: <strong>janusz.kuznik@devsmith.eu</strong>",
                        "privacyNote": "Administrator Serwisu nie udostępnia swojego adresu zamieszkania ani innych danych kontaktowych w treści niniejszego dokumentu z uwagi na ochronę prywatności. Dane te mogą zostać przekazane Klientowi lub właściwym organom wyłącznie na ich uzasadnione żądanie, zgodnie z obowiązującymi przepisami prawa.",
                        "processorNote": "Dane osobowe Użytkowników w Serwisie są przetwarzane przez Administratora Serwisu wyłącznie na zlecenie Klienta, który jest administratorem danych osobowych swoich pracowników, współpracowników i kontrahentów. Administrator Serwisu pełni w tym zakresie rolę <strong>podmiotu przetwarzającego (procesora)</strong> na podstawie umowy powierzenia przetwarzania danych osobowych. Klient może przetwarzać dane Użytkowników <strong>wyłącznie w zakresie i w sposób udostępniony przez Serwis</strong>; wszelkie inne działania są niedozwolone."
                    },
                    "section2": {
                        "title": "2. Zakres zbieranych danych",
                        "clientAccount": {
                            "title": "Dane dotyczące konta Klienta (firmowego):",
                            "companyName": "nazwa firmy,",
                            "nip": "numer identyfikacji podatkowej (NIP),",
                            "address": "adres siedziby firmy,",
                            "email": "adres poczty elektronicznej wskazany do kontaktu i rejestracji.",
                            "note": "Powyższe dane dotyczą podmiotów gospodarczych i nie stanowią co do zasady danych osobowych w rozumieniu RODO, z wyjątkiem przypadków, gdy adres poczty elektronicznej umożliwia identyfikację osoby fizycznej."
                        },
                        "userAccounts": {
                            "title": "Dane dotyczące kont Użytkowników:",
                            "name": "imię i nazwisko,",
                            "employeeId": "identyfikator pracowniczy (np. numer pracownika) nadany przez Klienta lub wygenerowany automatycznie,",
                            "optional": "opcjonalnie: adres poczty elektronicznej oraz numer telefonu (zazwyczaj służbowy), jeżeli przypisanie tych danych umożliwia korzystanie z dodatkowych funkcjonalności Serwisu, takich jak powiadomienia.",
                            "processorNote": "Administrator wskazuje, że nie jest bezpośrednim administratorem danych osobowych Użytkowników, lecz przetwarza je wyłącznie na podstawie <strong>umowy powierzenia przetwarzania danych osobowych</strong> zawartej z Klientem, który pełni rolę administratora danych swoich pracowników, współpracowników i kontrahentów.",
                            "purpose": "Dane Użytkowników przetwarzane są w zakresie niezbędnym do świadczenia usług oferowanych przez Serwis oraz w celu zapewnienia funkcjonalności przypisanych kont Użytkowników, w tym umożliwienia korzystania z przypisanych funkcji i powiadomień."
                        }
                    },
                    "section2a": {
                        "title": "2.a Dane techniczne i pliki cookies",
                        "intro": "Podczas korzystania z Serwisu mogą być przetwarzane dane techniczne dotyczące urządzenia Użytkownika, w tym m.in. typ przeglądarki internetowej, system operacyjny, ustawienia językowe oraz inne dane przekazywane automatycznie w ramach standardowej komunikacji sieciowej z Serwisem.",
                        "cookies": "Serwis może wykorzystywać <strong>pliki cookies niezbędne do prawidłowego działania systemu i zapewnienia bezpieczeństwa sesji Użytkownika</strong>. Administrator nie stosuje mechanizmów śledzących ani nie przetwarza danych w celach profilowania czy dostarczania spersonalizowanych treści.",
                        "ip": "W przypadku przyszłej implementacji funkcjonalności logowania, Serwis może przetwarzać również <strong>adres IP Użytkownika</strong> w celu zapewnienia bezpieczeństwa, monitorowania nadużyć i diagnozowania problemów technicznych.",
                        "purposeIntro": "Dane techniczne i informacje z cookies są przetwarzane wyłącznie w zakresie niezbędnym do:",
                        "purpose": {
                            "1": "zapewnienia prawidłowego działania Serwisu,",
                            "2": "utrzymania bezpieczeństwa systemu,",
                            "3": "diagnozowania i rozwiązywania problemów technicznych."
                        }
                    },
                    "section3": {
                        "title": "3. Cele przetwarzania danych osobowych",
                        "intro": "Dane osobowe Użytkowników przetwarzane są w Serwisie wyłącznie w zakresie i w celach niezbędnych do świadczenia usług oferowanych przez Serwis, w szczególności:",
                        "serviceManagement": {
                            "title": "Realizacja usług Serwisu",
                            "clientUsers": "umożliwienie Klientowi (firmie) zarządzania własnymi użytkownikami,",
                            "userFeatures": "umożliwienie Użytkownikom korzystania z funkcjonalności przypisanych do ich kont, w tym powiadomień i dostępu do treści Serwisu."
                        },
                        "accountManagement": {
                            "title": "Zarządzanie kontami i autoryzacja",
                            "integrity": "utrzymanie poprawności i integralności kont Klienta i jego Użytkowników,",
                            "security": "w przypadku wdrożenia logowania: zapewnienie bezpieczeństwa dostępu i autoryzacji."
                        },
                        "security": {
                            "title": "Bezpieczeństwo i diagnostyka techniczna",
                            "systemSafety": "zapewnienie bezpieczeństwa Serwisu i sesji użytkowników,",
                            "diagnostics": "monitorowanie poprawności działania systemu, diagnozowanie i rozwiązywanie problemów technicznych,",
                            "misuse": "ewentualne monitorowanie nadużyć w ramach systemu (w tym adresy IP w przyszłości)."
                        },
                        "clientSupport": {
                            "title": "Komunikacja z Klientem i wsparcie techniczne",
                            "contact": "umożliwienie kontaktu z Klientem w sprawach administracyjnych, technicznych lub związanych z realizacją usług Serwisu, w tym za pośrednictwem adresu e-mail podanego przy rejestracji konta firmowego,",
                            "support": "udzielanie pomocy technicznej i wsparcia w korzystaniu z Serwisu na podstawie danych Klienta lub Użytkowników."
                        }
                    },
                    "section4": {
                        "title": "4. Podstawa prawna przetwarzania danych osobowych",
                        "intro": "Przetwarzanie danych osobowych Użytkowników w Serwisie odbywa się na podstawie przepisów RODO, w szczególności:",
                        "art6b": {
                            "title": "Art. 6 ust. 1 lit. b RODO – niezbędność do wykonania umowy",
                            "desc": "Dane Użytkowników są przetwarzane w zakresie niezbędnym do świadczenia usług Serwisu na rzecz Klienta (firmy) oraz realizacji funkcjonalności przypisanych kontom użytkowników."
                        },
                        "art6f": {
                            "title": "Art. 6 ust. 1 lit. f RODO – prawnie uzasadniony interes administratora lub podmiotu przetwarzającego",
                            "desc": "Dane techniczne, w tym informacje o urządzeniu, sesjach i ewentualne adresy IP, są przetwarzane w celu zapewnienia bezpieczeństwa Serwisu, monitorowania poprawności działania systemu, diagnozowania problemów technicznych oraz zapobiegania nadużyciom. Prawnie uzasadniony interes obejmuje również możliwość udzielania pomocy technicznej Użytkownikom."
                        },
                        "art6c": {
                            "title": "Art. 6 ust. 1 lit. c RODO – obowiązek prawny",
                            "desc": "W zakresie, w jakim Serwis jest zobowiązany do realizacji obowiązków prawnych (np. przechowywanie danych niezbędnych do celów podatkowych lub rozliczeniowych Klienta), przetwarzanie odbywa się w celu wypełnienia tych obowiązków."
                        }
                    },
                    "section5": {
                        "title": "5. Okres przechowywania danych",
                        "clientData": "Dane Klienta (firmowe) przechowywane są przez cały okres świadczenia usług przez Serwis. W przypadku zakończenia korzystania z usług przez Klienta, dane Klienta oraz Użytkowników przypisanych bezpośrednio do danego Klienta przechowywane są dodatkowo przez okres do <strong>6 miesięcy</strong> w celu umożliwienia ewentualnego ponownego skorzystania z usług Serwisu i zachowania historii aktywności. Po upływie tego okresu dane Klienta oraz Użytkowników są <strong>trwale i nieodwracalnie usuwane lub anonimizowane</strong>, a ich odzyskanie nie jest możliwe.",
                        "userData": "Dane Użytkowników przechowywane są <strong>tak długo, jak Klient korzysta z Serwisu</strong>. Oznacza to, że w przypadku zakończenia współpracy Użytkownika z Klientem (np. rozwiązanie umowy o pracę, zakończenie współpracy kontraktowej) Użytkownik może utracić dostęp do systemu, jednak <strong>historia jego aktywności w Serwisie pozostaje zachowana na potrzeby Klienta</strong>, zgodnie z jego wymaganiami biznesowymi.",
                        "technicalLogs": "Dane techniczne i logi systemowe przechowywane są wyłącznie w zakresie niezbędnym do zapewnienia bezpieczeństwa Serwisu oraz diagnozowania i rozwiązywania problemów technicznych, przez okres nie dłuższy niż jest konieczny do realizacji tych celów."
                    },
                    "section6": {
                        "title": "6. Prawa użytkowników i Klientów",
                        "intro": "Użytkownicy mają prawo do:",
                        "access": "<strong>Dostępu do danych</strong> – prawo żądania od administratora (Klienta) informacji o przetwarzanych danych osobowych dotyczących Użytkownika, w tym celu, zakresie i okresie przechowywania.",
                        "correction": "<strong>Sprostowania danych</strong> – prawo żądania od administratora (Klienta) poprawienia lub uzupełnienia nieprawidłowych lub niekompletnych danych osobowych.",
                        "deletion": {
                            "title": "<strong>Usunięcia danych („prawo do bycia zapomnianym”)</strong> – prawo żądania od administratora (Klienta) usunięcia danych osobowych, o ile nie istnieją inne uzasadnione podstawy ich przechowywania.",
                            "note": "W zależności od potrzeb biznesowych Klienta, operacja usunięcia danych może być realizowana w jednym z dwóch wariantów:",
                            "permanent": "<strong>Nieodwracalne usunięcie konta Użytkownika wraz z całą historią jego aktywności</strong> w Serwisie.",
                            "anonymization": "<strong>Anonimizacja konta Użytkownika</strong>, w wyniku której dane osobowe nie są już powiązane z konkretną osobą, co pozwala zachować historię aktywności w systemie, zgodnie z wymaganiami Klienta.",
                            "processorNote": "Administrator Serwisu realizuje powyższe operacje wyłącznie na podstawie zlecenia Klienta, który jest administratorem danych osobowych Użytkowników."
                        },
                        "limitation": "<strong>Ograniczenia przetwarzania</strong> – prawo żądania od administratora (Klienta) ograniczenia przetwarzania danych w przypadkach przewidzianych przepisami RODO.",
                        "objection": "<strong>Sprzeciwu wobec przetwarzania danych</strong> – prawo wniesienia sprzeciwu wobec przetwarzania danych osobowych w przypadkach przewidzianych przepisami RODO.",
                        "complaint": "<strong>Skargi do organu nadzorczego</strong> – prawo wniesienia skargi do Prezesa Urzędu Ochrony Danych Osobowych w przypadku uznania, że przetwarzanie danych osobowych narusza przepisy RODO."
                    },
                    "section6a": {
                        "title": "6.a Usunięcie konta Klienta (firmowego)",
                        "clientRequest": "Klient ma prawo żądania usunięcia swojego konta w Serwisie.",
                        "irreversible": "Usunięcie konta Klienta jest <strong>nieodwracalne</strong> i skutkuje natychmiastowym usunięciem:",
                        "userAccounts": "kont wszystkich Użytkowników przypisanych do danego Klienta (pracowników, współpracowników, kontrahentów),",
                        "history": "całej historii danych związanych z kontem Klienta i jego Użytkowników."
                    },
                    "section7": {
                        "title": "7. Kontakt w sprawach ochrony danych osobowych",
                        "intro": "Wszelkie pytania, wnioski lub żądania dotyczące przetwarzania danych osobowych w związku z korzystaniem z Serwisu mogą być kierowane do Administratora Serwisu za pośrednictwem poczty elektronicznej:",
                        "email": "crewops@devsmith.eu",
                        "responseTime": "Administrator Serwisu rozpatruje zgłoszenia niezwłocznie, z uwzględnieniem terminów przewidzianych w art. 12 RODO."
                    },
                    "section8": {
                        "title": "8. Aktualizacja Polityki Prywatności",
                        "updateRight": "Administrator Serwisu zastrzega sobie prawo do <strong>aktualizowania niniejszej Polityki Prywatności</strong> w dowolnym czasie, w szczególności w przypadku zmian w funkcjonalności Serwisu, wymogów prawnych lub praktyk przetwarzania danych.",
                        "notification": "Administrator Systemu <strong>deklaruje, że będzie informował Klientów Serwisu o planowanej zmianie Polityki Prywatności</strong> z odpowiednim wyprzedzeniem, aby umożliwić im zapoznanie się z nowymi zapisami.",
                        "effective": "Aktualizacje Polityki wchodzą w życie z chwilą ich publikacji w Serwisie pod tym samym adresem URL. Zaleca się regularne zapoznawanie się z treścią Polityki w celu bieżącej informacji o sposobach przetwarzania danych osobowych."
                    },
                    "section9": {
                        "title": "9. Informacje o sporządzeniu dokumentu",
                        "intro": "Dokument Polityki Prywatności został przygotowany przez <strong>Administratora Serwisu</strong> w celu transparentnego przedstawienia zasad przetwarzania danych osobowych w Serwisie.",
                        "effectiveDate": "Data wprowadzenia w życie: <strong>1 października 2025 roku</strong>."
                    },
                    "footer": {
                        "copyright": "© 2025 CrewOps. Wszelkie prawa zastrzeżone.",
                        "author": "Projekt i wykonanie: Janusz Kuźnik"
                    }
                },
                "footer": {
                    "copyright": "© 2025 CrewOps. Wszelkie prawa zastrzeżone.",
                    "designedBy": "Zaprojektowany i zbudowany przez Janusz Kuźnik"
                }
            }
        };

        let currentLang = localStorage.getItem('preferred-language') || 'en';

        function updateContent(lang) {
            const translation = translations[lang] || translations.en;
            document.querySelectorAll('[data-i18n]').forEach(element => {
                const keys = element.getAttribute('data-i18n').split('.');
                let value = translation;
                // Navigate through the translation object
                for (const key of keys) {
                    if (value && typeof value === 'object' && key in value) {
                        value = value[key];
                    } else {
                        value = null;
                        break;
                    }
                }
                // Update element content if translation exists
                if (value) {
                    if (element.tagName === 'INPUT' || element.tagName === 'META') {
                        element.setAttribute('content', value);
                    } else {
                        // Check if the value contains HTML tags
                        if (typeof value === 'string' && (value.includes('<') || value.includes('&'))) {
                            element.innerHTML = value;
                        } else {
                            element.textContent = value;
                        }
                    }
                }
            });

            // Update document title
            const titleKey = document.documentElement.getAttribute('data-i18n-title');
            if (titleKey && translation[titleKey]) {
                document.title = translation[titleKey];
            }

            // Remove the loading class after a short delay
            setTimeout(() => {
                document.documentElement.classList.remove('i18n-loading');
            }, 50);
        }

        function setActiveLanguageButton(lang) {
            document.querySelectorAll('.lang-btn').forEach(btn => {
                btn.classList.remove('active');
                if (btn.id === `lang-${lang}`) {
                    btn.classList.add('active');
                }
            });
        }

        function changeLanguage(lang) {
            currentLang = lang;
            localStorage.setItem('preferred-language', lang);
            // Update lang attribute
            document.documentElement.lang = lang;
            setActiveLanguageButton(lang);

            // Add the loading class back before changing content
            document.documentElement.classList.add('i18n-loading');

            // Update content after a short delay
            setTimeout(() => {
                updateContent(lang);
            }, 10);
        }

        // Initialize language
        function initLanguage() {
            // Set lang attribute initially
            document.documentElement.lang = currentLang;
            setActiveLanguageButton(currentLang);
            updateContent(currentLang);
        }

        // Add event listeners to language buttons
        document.querySelectorAll('.lang-btn').forEach(button => {
            button.addEventListener('click', function() {
                const lang = this.id.replace('lang-', '');
                changeLanguage(lang);
            });
        });

        // Initialize on page load
        initLanguage();
    });