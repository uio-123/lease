import { useState, useEffect, useCallback } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { MapPin, Phone, Star, Wifi, Snowflake, Wind, Tv, Refrigerator, type LucideIcon, ArrowLeft } from 'lucide-react';
import { Swiper, Toast, ActionSheet, Button } from 'antd-mobile';
import { getApartmentDetailById, getRoomPageByApartmentId, saveOrUpdateAppointment } from '@/api';
import type { ApartmentDetailVo, RoomItemVo } from '@/types';
import './ApartmentDetailPage.css';

const facilityIcons: Record<string, LucideIcon> = {
  'wifi': Wifi,
  'ac': Snowflake,
  'fan': Wind,
  'tv': Tv,
  'fridge': Refrigerator,
};

export default function ApartmentDetailPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [detail, setDetail] = useState<ApartmentDetailVo | null>(null);
  const [rooms, setRooms] = useState<RoomItemVo[]>([]);
  const [loading, setLoading] = useState(true);

  const loadDetail = useCallback(async () => {
    if (!id) return;
    try {
      const res = await getApartmentDetailById(Number(id));
      setDetail(res.data);
    } catch {
      Toast.show('加载公寓详情失败');
    } finally {
      setLoading(false);
    }
  }, [id]);

  const loadRooms = useCallback(async () => {
    if (!id) return;
    try {
      const res = await getRoomPageByApartmentId(1, 10, Number(id));
      setRooms(res.data.records || []);
    } catch {
      Toast.show('加载房间列表失败');
    }
  }, [id]);

  useEffect(() => {
    loadDetail();
    loadRooms();
  }, [loadDetail, loadRooms]);

  const handleAppointment = () => {
    if (!detail) return;
    ActionSheet.show({
      actions: [
        {
          key: 'book',
          text: '立即预约',
          onClick: async () => {
            try {
              await saveOrUpdateAppointment({
                apartmentId: detail.id,
                appointmentTime: new Date(Date.now() + 24 * 60 * 60 * 1000).toISOString().split('T')[0],
              });
              Toast.show('预约成功');
            } catch {
              Toast.show('预约失败');
            }
          },
        },
      ],
      cancelText: '取消',
    });
  };

  const handleRoomClick = (roomId: number) => {
    navigate(`/room/${roomId}`);
  };

  if (loading) {
    return <div className="loading">加载中...</div>;
  }

  if (!detail) {
    return <div className="empty">公寓不存在</div>;
  }

  return (
    <div className="apartment-detail-page">
      <div className="detail-header-bar">
        <button className="back-btn" onClick={() => navigate(-1)}>
          <ArrowLeft className="back-icon" />
        </button>
      </div>
      <Swiper autoplay className="detail-swiper">
        {detail.graphUrlList?.length > 0 ? (
          detail.graphUrlList.map((url, index) => (
            <Swiper.Item key={index}>
              <img src={url} alt="" className="detail-img" />
            </Swiper.Item>
          ))
        ) : (
          <Swiper.Item>
            <img src={detail.photoUrl || 'https://imgbed.top/mock/375x250.jpg'} alt="" className="detail-img" />
          </Swiper.Item>
        )}
      </Swiper>

      <div className="detail-content">
        <div className="detail-header">
          <h1 className="detail-title">{detail.name}</h1>
          <div className="detail-location">
            <MapPin className="icon" />
            <span>{detail.provinceName} {detail.cityName} {detail.districtName} {detail.address}</span>
          </div>
          {detail.nearMetroStation && (
            <div className="near-metro">
              <Star className="icon" />
              <span>近{detail.nearMetroStation}</span>
            </div>
          )}
        </div>

        <div className="detail-section">
          <h2>公寓简介</h2>
          <p className="intro">{detail.intro || '暂无简介'}</p>
        </div>

        <div className="detail-section">
          <h2>特色标签</h2>
          <div className="labels">
            {detail.labels?.map((label) => (
              <span key={label.id} className="label-tag">
                {label.name}
              </span>
            ))}
          </div>
        </div>

        <div className="detail-section">
          <h2>房间设施</h2>
          <div className="facilities">
            {detail.facilities?.map((facility) => {
              const Icon = facilityIcons[facility.icon] || Star;
              return (
                <div key={facility.id} className="facility-item">
                  <Icon className="facility-icon" />
                  <span>{facility.name}</span>
                </div>
              );
            })}
            {(!detail.facilities || detail.facilities.length === 0) && (
              <span className="no-data">暂无设施信息</span>
            )}
          </div>
        </div>

        <div className="detail-section">
          <h2>费用说明</h2>
          <div className="fees">
            {detail.additionalFees?.map((fee) => (
              <div key={fee.id} className="fee-item">
                <span className="fee-name">{fee.feeName}</span>
                <span className="fee-price">¥{fee.feePrice}</span>
              </div>
            ))}
            <div className="fee-item">
              <span className="fee-name">租金</span>
              <span className="fee-price rent">¥{detail.minRent}起/月</span>
            </div>
            {(!detail.additionalFees || detail.additionalFees.length === 0) && (
              <span className="no-data">暂无费用信息</span>
            )}
          </div>
        </div>

        <div className="detail-section">
          <h2>房间列表</h2>
          <div className="room-list">
            {rooms.map((room) => (
              <div
                key={room.id}
                className="room-card"
                onClick={() => handleRoomClick(room.id)}
              >
                <img
                  src={room.photoUrl || 'https://imgbed.top/mock/100x100.jpg'}
                  alt={room.roomNumber}
                  className="room-img"
                />
                <div className="room-info">
                  <span className="room-number">{room.roomNumber}</span>
                  <span className="room-rent">¥{room.rent}/月</span>
                  <div className="room-tags">
                    {room.labels?.slice(0, 2).map((label, index) => (
                      <span key={index} className="room-tag">{label}</span>
                    ))}
                  </div>
                </div>
                <span className={`room-status ${room.isReleased ? 'available' : 'unavailable'}`}>
                  {room.isReleased ? '可租' : '已租'}
                </span>
              </div>
            ))}
            {rooms.length === 0 && <span className="no-data">暂无房间信息</span>}
          </div>
        </div>

        <div className="contact-bar">
          <a href={`tel:${detail.phone}`} className="contact-btn phone">
            <Phone className="icon" />
            <span>电话咨询</span>
          </a>
          <Button color="primary" className="appointment-btn" onClick={handleAppointment}>
            预约看房
          </Button>
        </div>
      </div>
    </div>
  );
}
