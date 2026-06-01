import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Search, MapPin, Calendar, FileText, Clock } from 'lucide-react';
import { Swiper, Toast } from 'antd-mobile';
import { getProvinceList, getCityListByProvinceId, getDistrictListByCityId, getApartmentListByQuery } from '@/api';
import type { ProvinceInfo, CityInfo, DistrictInfo, ApartmentItemVo } from '@/types';
import { Picker } from 'antd-mobile';
import './HomePage.css';

const districtNames: Record<number, string> = {};

export default function HomePage() {
  const navigate = useNavigate();
  const [provinces, setProvinces] = useState<ProvinceInfo[]>([]);
  const [cities, setCities] = useState<CityInfo[]>([]);
  const [districts, setDistricts] = useState<DistrictInfo[]>([]);
  const [apartments, setApartments] = useState<ApartmentItemVo[]>([]);
  const [loading, setLoading] = useState(false);

  const [selectedProvince, setSelectedProvince] = useState<ProvinceInfo | null>(null);
  const [selectedCity, setSelectedCity] = useState<CityInfo | null>(null);
  const [selectedDistrict, setSelectedDistrict] = useState<DistrictInfo | null>(null);

  useEffect(() => {
    loadProvinces();
  }, []);

  useEffect(() => {
    if (selectedProvince) {
      loadCities(selectedProvince.id);
    }
  }, [selectedProvince]);

  useEffect(() => {
    if (selectedCity) {
      loadDistricts(selectedCity.id);
    }
  }, [selectedCity]);

  useEffect(() => {
    if (selectedDistrict) {
      districtNames[selectedDistrict.id] = selectedDistrict.name;
    }
  }, [selectedDistrict]);

  useEffect(() => {
    loadApartments();
  }, []);

  const loadProvinces = async () => {
    try {
      const res = await getProvinceList();
      setProvinces(res.data || []);
    } catch {
      Toast.show('加载省份失败');
    }
  };

  const loadCities = async (provinceId: number) => {
    try {
      const res = await getCityListByProvinceId(provinceId);
      setCities(res.data || []);
    } catch {
      Toast.show('加载城市失败');
    }
  };

  const loadDistricts = async (cityId: number) => {
    try {
      const res = await getDistrictListByCityId(cityId);
      setDistricts(res.data || []);
    } catch {
      Toast.show('加载区县失败');
    }
  };

  const loadApartments = async () => {
    setLoading(true);
    try {
      const res = await getApartmentListByQuery({});
      setApartments(res.data || []);
    } catch {
      Toast.show('加载公寓失败');
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = async () => {
    setLoading(true);
    try {
      const res = await getApartmentListByQuery({
        provinceId: selectedProvince?.id,
        cityId: selectedCity?.id,
        districtId: selectedDistrict?.id,
      });
      setApartments(res.data || []);
    } catch {
      Toast.show('搜索失败');
    } finally {
      setLoading(false);
    }
  };

  const handleApartmentClick = (id: number) => {
    navigate(`/apartment/${id}`);
  };

  const provinceOptions = provinces.map((p) => ({
    label: p.name,
    value: p.id.toString(),
  }));

  const cityOptions = cities.map((c) => ({
    label: c.name,
    value: c.id.toString(),
  }));

  const districtOptions = districts.map((d) => ({
    label: d.name,
    value: d.id.toString(),
  }));

  return (
    <div className="home-page">
      <div className="search-bar">
        <div className="location-select">
          <MapPin className="icon" />
          <Picker
            columns={[provinceOptions, cityOptions, districtOptions]}
            value={[
              selectedProvince?.id?.toString(),
              selectedCity?.id?.toString(),
              selectedDistrict?.id?.toString(),
            ].filter(Boolean) as string[]}
            onChange={(val) => {
              const [provinceId, cityId, districtId] = val || [];
              const province = provinces.find((p) => p.id.toString() === provinceId);
              const city = cities.find((c) => c.id.toString() === cityId);
              const district = districts.find((d) => d.id.toString() === districtId);
              setSelectedProvince(province || null);
              setSelectedCity(city || null);
              setSelectedDistrict(district || null);
            }}
          >
            {(items) => (
              <div className="picker-trigger">
                {items[2]?.label || items[1]?.label || items[0]?.label || '选择区域'}
              </div>
            )}
          </Picker>
        </div>
        <button className="search-btn" onClick={handleSearch}>
          <Search /> 搜索
        </button>
      </div>

      <Swiper autoplay className="banner-swiper">
        <Swiper.Item>
          <div className="banner-item banner-1">
            <h3>放心租 住好房</h3>
            <p>优质公寓精选</p>
          </div>
        </Swiper.Item>
        <Swiper.Item>
          <div className="banner-item banner-2">
            <h3>VR看房</h3>
            <p>足不出户看遍全城好房</p>
          </div>
        </Swiper.Item>
      </Swiper>

      <div className="quick-nav">
        <div className="nav-item" onClick={() => navigate('/appointments')}>
          <Calendar className="nav-icon" />
          <span>我的预约</span>
        </div>
        <div className="nav-item" onClick={() => navigate('/agreements')}>
          <FileText className="nav-icon" />
          <span>我的租约</span>
        </div>
        <div className="nav-item" onClick={() => navigate('/history')}>
          <Clock className="nav-icon" />
          <span>浏览历史</span>
        </div>
      </div>

      <div className="section-title">
        <h2>推荐公寓</h2>
        <span className="more" onClick={handleSearch}>更多&gt;</span>
      </div>

      <div className="apartment-list">
        {apartments.map((apt) => (
          <div
            key={apt.id}
            className="apartment-card"
            onClick={() => handleApartmentClick(apt.id)}
          >
            <img
              src={apt.photoUrl || 'https://imgbed.top/mock/200x150.jpg'}
              alt={apt.name}
              className="apartment-img"
            />
            <div className="apartment-info">
              <h3 className="apartment-name">{apt.name}</h3>
              <p className="apartment-address">
                <MapPin className="icon" />
                {apt.districtName} | {apt.address}
              </p>
              <div className="apartment-tags">
                {apt.labels?.slice(0, 3).map((label, index) => (
                  <span key={index} className="tag">{label}</span>
                ))}
              </div>
              <p className="apartment-price">
                <span className="price">¥{apt.minRent}</span>
                <span className="unit">/月起</span>
              </p>
            </div>
          </div>
        ))}
        {apartments.length === 0 && !loading && (
          <div className="empty-tip">暂无公寓信息</div>
        )}
      </div>
    </div>
  );
}
