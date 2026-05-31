import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import { AuthProvider } from "./context/AuthContext.jsx";
import Layout from "./components/Layout.jsx";
import BoardPage from "./pages/BoardPage.jsx";
import CabinetPage from "./pages/CabinetPage.jsx";
import HuntersPage from "./pages/HuntersPage.jsx";
import OrderDetailsPage from "./pages/OrderDetailsPage.jsx";

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Layout>
          <Routes>
            <Route path="/" element={<BoardPage />} />
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
