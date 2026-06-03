import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Clock } from 'lucide-react';
import { InfiniteScroll, Toast } from 'antd-mobile';
import { getBrowsingHistoryPage } from '@/api';
import type { HistoryItemVo } from '@/types';
import './HistoryPage.css';

export default function HistoryPage() {
  const navigate = useNavigate();
  const [history, setHistory] = useState<HistoryItemVo[]>([]);
  const [current, setCurrent] = useState(1);
  const [hasMore, setHasMore] = useState(true);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    let cancelled = false;
    const loadHistory = async () => {
      if (cancelled) return;
      setLoading(true);
      try {
        const res = await getBrowsingHistoryPage(1, 10);
        if (cancelled) return;
        setHistory(res.data.records || []);
        setHasMore(1 < res.data.pages);
        setCurrent(1);
      } catch {
        Toast.show('加载浏览历史失败');
      } finally {
        if (!cancelled) setLoading(false);
      }
    };
    loadHistory();
    return () => { cancelled = true; };
  }, []);

  const loadMore = async () => {
    if (loading || !hasMore) return;
    const nextPage = current + 1;
    setLoading(true);
    try {
      const res = await getBrowsingHistoryPage(nextPage, 10);
      setHistory((prev) => [...prev, ...(res.data.records || [])]);
      setHasMore(nextPage < res.data.pages);
      setCurrent(nextPage);
    } catch {
      Toast.show('加载浏览历史失败');
    } finally {
      setLoading(false);
    }
  };

  const handleItemClick = (apartmentId: number) => {
    navigate(`/apartment/${apartmentId}`);
  };

  const formatTime = (time: string) => {
    const date = new Date(time);
    const now = new Date();
    const diff = now.getTime() - date.getTime();
    const days = Math.floor(diff / (1000 * 60 * 60 * 24));
    if (days === 0) return '今天';
    if (days === 1) return '昨天';
    if (days < 7) return `${days}天前`;
    return time.split(' ')[0];
  };

  return (
    <div className="history-page">
      <div className="history-list">
        {history.map((item) => (
          <div key={item.id} className="history-card" onClick={() => handleItemClick(item.apartmentId)}>
            <img
              src={item.apartmentPhotoUrl || 'https://imgbed.top/mock/80x80.jpg'}
              alt={item.apartmentName}
              className="apartment-img"
            />
            <div className="history-info">
              <h3>{item.apartmentName}</h3>
              <p className="room">房间号：{item.roomNumber}</p>
              <p className="price">¥{item.rent}/月</p>
            </div>
            <div className="history-time">
              <Clock className="icon" />
              <span>{formatTime(item.browsingTime)}</span>
            </div>
          </div>
        ))}
        {history.length === 0 && !loading && (
          <div className="empty">暂无浏览记录</div>
        )}
      </div>
      <InfiniteScroll loadMore={loadMore} hasMore={hasMore} />
    </div>
  );
}
