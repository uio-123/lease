import { BrowserRouter, Routes, Route, Navigate, useNavigate, useLocation } from 'react-router-dom';
import { TabBar } from 'antd-mobile';
import { Home, FileText, Calendar, User } from 'lucide-react';
import { useAuthStore } from '@/store/auth';
import {
  LoginPage,
  HomePage,
  ApartmentDetailPage,
  RoomDetailPage,
  AppointmentListPage,
  AgreementListPage,
  HistoryPage,
  ProfilePage,
} from '@/pages';
import './App.css';

const tabItems = [
  { key: '/', title: '首页', icon: <Home /> },
  { key: '/agreements', title: '租约', icon: <FileText /> },
  { key: '/appointments', title: '预约', icon: <Calendar /> },
  { key: '/profile', title: '我的', icon: <User /> },
];

function App() {
  const { token } = useAuthStore();

  return (
    <BrowserRouter
      future={{
        v7_startTransition: true,
        v7_relativeSplatPath: true,
      }}
    >
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route
          path="/*"
          element={
            token ? (
              <MainLayout />
            ) : (
              <Navigate to="/login" replace />
            )
          }
        />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  );
}

function MainLayout() {
  const navigate = useNavigate();
  const location = useLocation();
  const { token } = useAuthStore();

  if (!token) {
    return <Navigate to="/login" replace />;
  }

  // 从当前 pathname 推导活跃 tab
  const activeKey = tabItems.some((item) => item.key === location.pathname)
    ? location.pathname
    : '/';

  return (
    <div className="main-layout">
      <div className="content">
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="apartment/:id" element={<ApartmentDetailPage />} />
          <Route path="room/:id" element={<RoomDetailPage />} />
          <Route path="appointments" element={<AppointmentListPage />} />
          <Route path="agreements" element={<AgreementListPage />} />
          <Route path="history" element={<HistoryPage />} />
          <Route path="profile" element={<ProfilePage />} />
        </Routes>
      </div>
      <TabBar
        className="tab-bar"
        activeKey={activeKey}
        onChange={(key) => navigate(key)}
      >
        {tabItems.map((item) => (
          <TabBar.Item key={item.key} icon={item.icon} title={item.title} />
        ))}
      </TabBar>
    </div>
  );
}

export default App;
