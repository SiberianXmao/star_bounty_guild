import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import { AuthProvider } from "./context/AuthContext.jsx";
import Layout from "./components/layout/Layout.jsx";
import AuthPage from "./pages/auth/AuthPage.jsx";
import BoardPage from "./pages/board/BoardPage.jsx";
import CabinetPage from "./pages/cabinet/CabinetPage.jsx";
import HuntersPage from "./pages/hunters/HuntersPage.jsx";
import OrderDetailsPage from "./pages/order-details/OrderDetailsPage.jsx";

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Layout>
          <Routes>
            <Route path="/" element={<BoardPage />} />
            <Route path="/auth" element={<AuthPage />} />
            <Route path="/orders/:orderId" element={<OrderDetailsPage />} />
            <Route path="/hunters" element={<HuntersPage />} />
            <Route path="/cabinet" element={<CabinetPage />} />
            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </Layout>
      </AuthProvider>
    </BrowserRouter>
  );
}
