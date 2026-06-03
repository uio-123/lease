import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Calendar, Banknote } from 'lucide-react';
import { Tabs, Toast } from 'antd-mobile';
import { getAgreementList, updateAgreementStatus } from '@/api';
import type { AgreementItemVo } from '@/types';
import './AgreementListPage.css';

const statusMap: Record<string, { color: string; label: string }> = {
  SIGNING: { color: '#faad14', label: '待签约' },
  EXECUTE: { color: '#52c41a', label: '生效中' },
  TERMINATION: { color: '#999', label: '已终止' },
  RENEWAL: { color: '#1890ff', label: '续约中' },
};

export default function AgreementListPage() {
  const navigate = useNavigate();
  const [agreements, setAgreements] = useState<AgreementItemVo[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadAgreements();
  }, []);

  const loadAgreements = async () => {
    try {
      const res = await getAgreementList();
      setAgreements(res.data || []);
    } catch {
      Toast.show('加载租约列表失败');
    } finally {
      setLoading(false);
    }
  };

  const handleItemClick = (id: number) => {
    navigate(`/agreement/${id}`);
  };

  const handleConfirm = async (id: number, e: React.MouseEvent) => {
    e.stopPropagation();
    try {
      await updateAgreementStatus(id, 'EXECUTE');
      Toast.show('确认成功');
      loadAgreements();
    } catch {
      Toast.show('确认失败');
    }
  };

  const handleTerminate = async (id: number, e: React.MouseEvent) => {
    e.stopPropagation();
    try {
      await updateAgreementStatus(id, 'TERMINATION');
      Toast.show('退租成功');
      loadAgreements();
    } catch {
      Toast.show('退租失败');
    }
  };

  const activeAgreements = agreements.filter((a) => a.leaseStatus === 'EXECUTE' || a.leaseStatus === 'SIGNING');
  const allAgreements = agreements;

  const renderAgreementCard = (item: AgreementItemVo) => {
    const status = statusMap[item.leaseStatus] || { color: '#999', label: '未知' };
    return (
      <div key={item.id} className="agreement-card" onClick={() => handleItemClick(item.id)}>
        <div className="card-header">
          <img
            src={item.apartmentPhotoUrl || 'https://imgbed.top/mock/60x60.jpg'}
            alt={item.apartmentName}
            className="apartment-img"
          />
          <div className="header-info">
            <h3>{item.apartmentName}</h3>
            <p className="room-number">房间号：{item.roomNumber}</p>
          </div>
          <span className="status" style={{ background: status.color }}>
            {status.label}
          </span>
        </div>
        <div className="card-body">
          <div className="info-item">
            <Calendar className="icon" />
            <span>{item.leaseStartDate} ~ {item.leaseEndDate}</span>
          </div>
          <div className="info-item">
            <Banknote className="icon" />
            <span>¥{item.rent}/{item.paymentType}</span>
          </div>
        </div>
        <div className="card-footer">
          {item.leaseStatus === 'SIGNING' && (
            <button className="action-btn confirm" onClick={(e) => handleConfirm(item.id, e)}>
              确认签约
            </button>
          )}
          {item.leaseStatus === 'EXECUTE' && (
            <button className="action-btn terminate" onClick={(e) => handleTerminate(item.id, e)}>
              申请退租
            </button>
          )}
        </div>
      </div>
    );
  };

  return (
    <div className="agreement-list-page">
      <Tabs defaultActiveKey="active">
        <Tabs.Tab title={`进行中(${activeAgreements.length})`} key="active">
          <div className="tab-content">
            {activeAgreements.map(renderAgreementCard)}
            {activeAgreements.length === 0 && !loading && (
              <div className="empty">暂无进行中的租约</div>
            )}
          </div>
        </Tabs.Tab>
        <Tabs.Tab title={`全部(${allAgreements.length})`} key="all">
          <div className="tab-content">
            {allAgreements.map(renderAgreementCard)}
            {allAgreements.length === 0 && !loading && (
              <div className="empty">暂无租约记录</div>
            )}
          </div>
        </Tabs.Tab>
      </Tabs>
    </div>
  );
}
