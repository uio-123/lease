import { useNavigate } from 'react-router-dom';
import { User, Settings, FileText, Calendar, Clock, LogOut } from 'lucide-react';
import { Dialog, Toast } from 'antd-mobile';
import { useAuthStore } from '@/store/auth';
import './ProfilePage.css';

export default function ProfilePage() {
  const navigate = useNavigate();
  const { userInfo, logout } = useAuthStore();

  const handleLogout = () => {
    Dialog.confirm({
      content: '确定要退出登录吗？',
      onConfirm: () => {
        logout();
        Toast.show('已退出登录');
        navigate('/login', { replace: true });
      },
    });
  };

  const menuItems = [
    { icon: FileText, label: '我的租约', path: '/agreements' },
    { icon: Calendar, label: '我的预约', path: '/appointments' },
    { icon: Clock, label: '浏览历史', path: '/history' },
    { icon: Setting, label: '设置', path: '/settings' },
  ];

  return (
    <div className="profile-page">
      <div className="profile-header">
        <div className="avatar">
          {userInfo?.avatarUrl ? (
            <img src={userInfo.avatarUrl} alt="头像" />
          ) : (
            <User className="default-avatar" />
          )}
        </div>
        <div className="user-info">
          <h2>{userInfo?.nickname || userInfo?.phone || '用户'}</h2>
          <p>{userInfo?.phone}</p>
        </div>
      </div>

      <div className="menu-section">
        {menuItems.map((item) => (
          <div
            key={item.path}
            className="menu-item"
            onClick={() => navigate(item.path)}
          >
            <item.icon className="menu-icon" />
            <span>{item.label}</span>
            <span className="arrow">&gt;</span>
          </div>
        ))}
      </div>

      <div className="logout-section">
        <button className="logout-btn" onClick={handleLogout}>
          <LogOut className="btn-icon" />
          退出登录
        </button>
      </div>
    </div>
  );
}
