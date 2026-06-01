import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { MapPin, Calendar, Banknote, CheckCircle, ArrowLeft } from 'lucide-react';
import { Swiper, Toast, Picker, Button } from 'antd-mobile';
import { getRoomDetailById, getPaymentTypeListByRoomId, getLeaseTermListByRoomId, saveOrUpdateAgreement } from '@/api';
import type { RoomDetailVo, PaymentType, LeaseTerm } from '@/types';
import './RoomDetailPage.css';

export default function RoomDetailPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [room, setRoom] = useState<RoomDetailVo | null>(null);
  const [paymentTypes, setPaymentTypes] = useState<PaymentType[]>([]);
  const [leaseTerms, setLeaseTerms] = useState<LeaseTerm[]>([]);
  const [selectedPayment, setSelectedPayment] = useState<PaymentType | null>(null);
  const [selectedTerm, setSelectedTerm] = useState<LeaseTerm | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (id) {
      loadRoomDetail();
      loadPaymentTypes();
      loadLeaseTerms();
    }
  }, [id]);

  const loadRoomDetail = async () => {
    try {
      const res = await getRoomDetailById(Number(id));
      setRoom(res.data);
    } catch {
      Toast.show('加载房间详情失败');
    } finally {
      setLoading(false);
    }
  };

  const loadPaymentTypes = async () => {
    try {
      const res = await getPaymentTypeListByRoomId(Number(id));
      setPaymentTypes(res.data || []);
    } catch {
      Toast.show('加载支付方式失败');
    }
  };

  const loadLeaseTerms = async () => {
    try {
      const res = await getLeaseTermListByRoomId(Number(id));
      setLeaseTerms(res.data || []);
    } catch {
      Toast.show('加载租期失败');
    }
  };

  const handleSignAgreement = async () => {
    if (!selectedPayment || !selectedTerm) {
      Toast.show('请选择支付方式和租期');
      return;
    }
    try {
      await saveOrUpdateAgreement({
        roomId: Number(id),
        leaseStartDate: new Date().toISOString().split('T')[0],
        leaseEndDate: new Date(Date.now() + selectedTerm.count * 30 * 24 * 60 * 60 * 1000).toISOString().split('T')[0],
        leaseStatus: 'SIGNING' as any,
      });
      Toast.show('签约成功');
      navigate('/agreements');
    } catch {
      Toast.show('签约失败');
    }
  };

  const paymentOptions = paymentTypes.map((p) => ({
    label: p.name,
    value: p.id.toString(),
  }));

  const termOptions = leaseTerms.map((t) => ({
    label: `${t.count} ${t.unit}`,
    value: t.id.toString(),
  }));

  if (loading) {
    return <div className="loading">加载中...</div>;
  }

  if (!room) {
    return <div className="empty">房间不存在</div>;
  }

  return (
    <div className="room-detail-page">
      <div className="detail-header-bar">
        <button className="back-btn" onClick={() => navigate(-1)}>
          <ArrowLeft className="back-icon" />
        </button>
      </div>
      <Swiper autoplay className="room-swiper">
        {room.graphUrlList?.length > 0 ? (
          room.graphUrlList.map((url, index) => (
            <Swiper.Item key={index}>
              <img src={url} alt="" className="room-img" />
            </Swiper.Item>
          ))
        ) : (
          <Swiper.Item>
            <img src={room.photoUrl || 'https://imgbed.top/mock/375x300.jpg'} alt="" className="room-img" />
          </Swiper.Item>
        )}
      </Swiper>

      <div className="room-content">
        <div className="room-header">
          <h1 className="room-number">{room.roomNumber}</h1>
          <div className="room-apartment">
            <MapPin className="icon" />
            <span>{room.apartmentName}</span>
          </div>
        </div>

        <div className="room-price">
          <span className="price">¥{room.rent}</span>
          <span className="unit">/月</span>
        </div>

        <div className="room-section">
          <h2>房间标签</h2>
          <div className="tags">
            {room.labels?.map((label) => (
              <span key={label.id} className="tag">
                <CheckCircle className="tag-icon" />
                {label.name}
              </span>
            ))}
          </div>
        </div>

        <div className="room-section">
          <h2>房间配置</h2>
          <div className="attributes">
            {room.attributes?.map((attr, index) => (
              <div key={index} className="attr-item">
                <span className="attr-name">{attr.attrName}</span>
                <span className="attr-value">{attr.attrValue}</span>
              </div>
            ))}
            {(!room.attributes || room.attributes.length === 0) && (
              <span className="no-data">暂无配置信息</span>
            )}
          </div>
        </div>

        <div className="room-section">
          <h2>配套设施</h2>
          <div className="facilities">
            {room.facilities?.map((facility) => (
              <span key={facility.id} className="facility-tag">{facility.name}</span>
            ))}
            {(!room.facilities || room.facilities.length === 0) && (
              <span className="no-data">暂无设施信息</span>
            )}
          </div>
        </div>

        <div className="select-section">
          <div className="select-item">
            <div className="select-label">
              <Banknote className="label-icon" />
              <span>支付方式</span>
            </div>
            <Picker
              columns={[paymentOptions]}
              value={selectedPayment?.id?.toString()}
              onChange={(val) => {
                const payment = paymentTypes.find((p) => p.id.toString() === val?.[0]);
                setSelectedPayment(payment || null);
              }}
            >
              {(items) => (
                <div className="select-trigger">
                  {items[0]?.label || selectedPayment?.name || '请选择'}
                </div>
              )}
            </Picker>
          </div>

          <div className="select-item">
            <div className="select-label">
              <Calendar className="label-icon" />
              <span>租期</span>
            </div>
            <Picker
              columns={[termOptions]}
              value={selectedTerm?.id?.toString()}
              onChange={(val) => {
                const term = leaseTerms.find((t) => t.id.toString() === val?.[0]);
                setSelectedTerm(term || null);
              }}
            >
              {(items) => (
                <div className="select-trigger">
                  {items[0]?.label || (selectedTerm ? `${selectedTerm.count} ${selectedTerm.unit}` : '请选择')}
                </div>
              )}
            </Picker>
          </div>
        </div>
      </div>

      <div className="sign-bar">
        <div className="total-price">
          <span className="label">合计</span>
          <span className="price">¥{room.rent}</span>
        </div>
        <Button
          color="primary"
          className="sign-btn"
          onClick={handleSignAgreement}
          disabled={!room.isReleased}
        >
          {room.isReleased ? '立即签约' : '已租出'}
        </Button>
      </div>
    </div>
  );
}
