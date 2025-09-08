document.addEventListener('DOMContentLoaded', function() {
    // Embedded translations to avoid CORS issues when opening files directly
    const translations = {
        en: {
            "pageTitle": "CrewOps - Efficient Management of Field Teams and Machinery",
            "pageTitleProject": "Project Details - CrewOps",
            "pageTitleTech": "Technology Stack - CrewOps",
            "pageTitleAbout": "About Me - CrewOps",
            "nav": {
                "home": "Home",
                "project": "Project Details",
                "tech": "Tech Stack",
                "about": "About Me"
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
            "footer": {
                "copyright": "© 2025 CrewOps. All rights reserved.",
                "designedBy": "Designed and built by Janusz Kuźnik"
            }
        },
        pl: {
            "pageTitle": "CrewOps - Efektywne Zarządzanie Zespołami Terenowymi i Maszynami",
            "pageTitleProject": "Szczegóły Projektu - CrewOps",
            "pageTitleTech": "Stos Technologiczny - CrewOps",
            "pageTitleAbout": "O Mnie - CrewOps",
            "nav": {
                "home": "Strona Główna",
                "project": "Szczegóły Projektu",
                "tech": "Stos Technologiczny",
                "about": "O Mnie"
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
                    element.textContent = value;
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