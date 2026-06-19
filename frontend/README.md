# Frontend

React-интерфейс Bounty Guild.

## Содержит

- доску заказов и страницу заказа;
- каталог охотников;
- регистрацию и вход;
- личный кабинет заказчика и охотника;
- панели администратора и модератора.

Frontend обращается к backend через gateway по префиксу `/api/v1`.

## Локальная разработка

```powershell
npm install
npm run dev
```

Dev server: http://localhost:5173

Production build:

```powershell
npm run build
```

В Docker приложение доступно через gateway: http://localhost:3000
