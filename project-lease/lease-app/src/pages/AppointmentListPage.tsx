import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { MapPin, Calendar, Clock } from 'lucide-react';
import { Tabs, Toast } from 'antd-mobile';
import { getAppointmentList, deleteAppointment } from '@/api';
import type { AppointmentItemVo, AppointmentStatus } from '@/types';
import './AppointmentListPage.css';

const statusMap: Record<string, { color: string; label: string }> = {
  PENDING: { color: '#faad14', label: '待确认' },
  CONFIRMED: { color: '#52c41a', label: '已确认' },
  CANCELLED: { color: '#999', label: '已取消' },
  COMPLETED: { color: '#1890ff', label: '已完成' },
};

export default function AppointmentListPage() {
  const navigate = useNavigate();
  const [appointments, setAppointments] = useState<AppointmentItemVo[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadAppointments();
  }, []);

  const loadAppointments = async () => {
    try {
      const res = await getAppointmentList();
      setAppointments(res.data || []);
    } catch {
      Toast.show('加载预约列表失败');
    } finally {
      setLoading(false);
    }
  };

  const handleItemClick = (id: number) => {
    navigate(`/appointment/${id}`);
  };

  const handleCancel = async (id: number, e: React.MouseEvent) => {
    e.stopPropagation();
    try {
      await deleteAppointment(id);
      Toast.show('取消成功');
      loadAppointments();
    } catch {
      Toast.show('取消失败');
    }
  };

  const pendingAppointments = appointments.filter((a) => a.appointmentStatus === 'PENDING');
  const otherAppointments = appointments.filter((a) => a.appointmentStatus !== 'PENDING');

  const renderAppointmentCard = (item: AppointmentItemVo) => {
    const status = statusMap[item.appointmentStatus] || { color: '#999', label: '未知' };
    return (
      <div key={item.id} className="appointment-card" onClick={() => handleItemClick(item.id)}>
        <img
          src={item.apartmentPhotoUrl || 'https://imgbed.top/mock/100x100.jpg'}
          alt={item.apartmentName}
          className="apartment-img"
        />
        <div className="appointment-info">
          <h3 className="apartment-name">{item.apartmentName}</h3>
          <p className="location">
            <MapPin className="icon" />
            {item.provinceName} {item.cityName} {item.districtName} {item.address}
          </p>
          <p className="appointment-time">
            <Calendar className="icon" />
            {item.appointmentTime}
          </p>
          <div className="card-footer">
            <span className="status" style={{ background: status.color }}>
              {status.label}
            </span>
            {(item.appointmentStatus === 'PENDING') && (
              <button className="cancel-btn" onClick={(e) => handleCancel(item.id!, e)}>
                取消预约
              </button>
            )}
          </div>
        </div>
      </div>
    );
  };

  return (
    <div className="appointment-list-page">
      <Tabs defaultActiveKey="pending">
        <Tabs.Tab title={`待确认(${pendingAppointments.length})`} key="pending">
          <div className="tab-content">
            {pendingAppointments.map(renderAppointmentCard)}
            {pendingAppointments.length === 0 && !loading && (
              <div className="empty">暂无待确认的预约</div>
            )}
          </div>
        </Tabs.Tab>
        <Tabs.Tab title={`全部(${appointments.length})`} key="all">
          <div className="tab-content">
            {appointments.map(renderAppointmentCard)}
            {appointments.length === 0 && !loading && (
              <div className="empty">暂无预约记录</div>
            )}
          </div>
        </Tabs.Tab>
      </Tabs>
    </div>
  );
}
