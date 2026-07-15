import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import { AuthProvider } from "./context/AuthContext.jsx";
import Layout from "./components/layout/Layout.jsx";
import ScrollToTop from "./components/layout/ScrollToTop.jsx";
import AuthPage from "./pages/auth/AuthPage.jsx";
import AuthCallbackPage from "./pages/auth-callback/AuthCallbackPage.jsx";
import AdminPage from "./pages/admin/AdminPage.jsx";
import BoardPage from "./pages/board/BoardPage.jsx";
import CabinetPage from "./pages/cabinet/CabinetPage.jsx";
import DirectoryPage from "./pages/directory/DirectoryPage.jsx";
import DirectoryDetailsPage from "./pages/directory-details/DirectoryDetailsPage.jsx";
import HunterDetailsPage from "./pages/hunter-details/HunterDetailsPage.jsx";
import HuntersPage from "./pages/hunters/HuntersPage.jsx";
import ModerationPage from "./pages/moderation/ModerationPage.jsx";
import OrderDetailsPage from "./pages/order-details/OrderDetailsPage.jsx";

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <ScrollToTop />
        <Layout>
          <Routes>
            <Route path="/" element={<BoardPage />} />
            <Route path="/admin" element={<AdminPage />} />
            <Route path="/auth" element={<AuthPage />} />
            <Route path="/auth/callback" element={<AuthCallbackPage />} />
            <Route path="/moderation" element={<ModerationPage />} />
            <Route path="/orders/:orderId" element={<OrderDetailsPage />} />
            <Route path="/hunters" element={<HuntersPage />} />
            <Route path="/hunters/:hunterId" element={<HunterDetailsPage />} />
            <Route path="/cabinet" element={<CabinetPage />} />
            <Route path="/directory" element={<DirectoryPage />} />
            <Route path="/directory/:entityType/:entityId" element={<DirectoryDetailsPage />} />
            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </Layout>
      </AuthProvider>
    </BrowserRouter>
  );
}
