import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { TabBar } from 'antd-mobile';
import { Home, FileText, Calendar, User } from 'lucide-react';
import { useState } from 'react';
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
  const [activeTab, setActiveTab] = useState('/');
  const { token } = useAuthStore();

  const isAuthPage = (path: string) => ['/login'].includes(path);

  const showTabBar = !isAuthPage(activeTab) && token;

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
              <MainLayout activeTab={activeTab} onTabChange={setActiveTab} />
            ) : (
              <Navigate to="/login" replace />
            )
          }
        >
          <Route index element={<HomePage />} />
          <Route path="apartment/:id" element={<ApartmentDetailPage />} />
          <Route path="room/:id" element={<RoomDetailPage />} />
          <Route path="appointments" element={<AppointmentListPage />} />
          <Route path="agreements" element={<AgreementListPage />} />
          <Route path="history" element={<HistoryPage />} />
          <Route path="profile" element={<ProfilePage />} />
        </Route>
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
      {showTabBar && (
        <TabBar className="tab-bar" onChange={(key) => setActiveTab(key)}>
          {tabItems.map((item) => (
            <TabBar.Item key={item.key} icon={item.icon} title={item.title} />
          ))}
        </TabBar>
      )}
    </BrowserRouter>
  );
}

function MainLayout({
  activeTab,
  onTabChange,
}: {
  activeTab: string;
  onTabChange: (key: string) => void;
}) {
  const { token } = useAuthStore();

  if (!token) {
    return <Navigate to="/login" replace />;
  }

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
      <TabBar className="tab-bar" activeKey={activeTab} onChange={onTabChange}>
        {tabItems.map((item) => (
          <TabBar.Item key={item.key} icon={item.icon} title={item.title} />
        ))}
      </TabBar>
    </div>
  );
}

export default App;
