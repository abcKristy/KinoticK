# KinoticK 🎬🦝 — приложение для заказа билетов в кино  

## О названии  
Название **KinoticK** — это игра слов:  
- **Kino** (кино) + **Ticket** (билет) → **KinoticK** (произносится как "кинотик").  
- Символ енота (**🦝**) добавляет игривость и запоминаемость, ассоциируясь с любопытством и активностью — как у зрителя в кинотеатре!  

## Описание  
KinoticK — это мобильное приложение для Android, позволяющее удобно бронировать билеты в кино. Здесь можно:  
- Просматривать афишу фильмов.  
- Выбирать места в зале.  
- Оплачивать билеты.  
- Сохранять историю заказов.  

---  

## Основной функционал  
1. **Фильмы и афиша**  
   - `MoviesFragment` + `fragment_movies.xml` — список фильмов с постерами.  
   - `MovieAdapter` — адаптер для отображения фильмов.  
   - `MovieDatabaseHelper` — база данных с информацией о фильмах.  

2. **Бронирование билетов**  
   - `OrderActivity` + `activity_order.xml` — выбор даты, времени и мест.  
   - `TicketsAdapter` — отображение купленных билетов.  

3. **Оплата**  
   - `PaymentActivity` + `activity_payment.xml` — экран с выбором способа оплаты.  

4. **Профиль пользователя**  
   - `ProfileFragment` + `fragment_profile.xml` — личные данные и история заказов.  

5. **Главный экран**  
   - `HomeFragment` + `fragment_home.xml` — баннеры и акции.  

6. **Авторизация**  
   - `LoginFragment` + `fragment_login.xml` — вход в аккаунт.  

7. **Навигация**  
   - `MainActivity` + `bottom_nav_menu.xml` — нижнее меню для перехода между разделами.  

---  

## Установка  
1. Клонируйте репозиторий:  
   ```bash  
   git clone https://github.com/abcKristy/KinoticK.git



graph TD
    %% Клиентская часть (Android)
    A[Client: Android App] -->|HTTP/API Calls| B[Backend: Spring Boot]
    A --> C[UI: Jetpack Compose]
    A --> D[Local Data: Room DB]
    A --> E[Auth: Firebase]

    %% Бэкенд
    B -->|SQL| F[Database: PostgreSQL]
    B --> G[API: REST]
    B --> H[Payment Gateway]

    %% Инфраструктура
    I[Docker] -->|Container| B
    I -->|Container| F
    J[CI/CD: GitHub Actions] -->|Deploy| I

    %% Внешние сервисы
    B -->|JSON| K[External Cinema APIs]
    E -->|OAuth| L[Google/Facebook Auth]
    H -->|Stripe API| M[Payment Processors]

    %% Группировка блоков
    subgraph "Mobile Layer (Android)"
        A
        C
        D
        E
    end

    subgraph "Backend Layer"
        B
        G
        F
    end

    subgraph "Infrastructure"
        I
        J
    end

    subgraph "External Services"
        K
        L
        M
    end   
