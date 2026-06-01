// 地区信息
export interface ProvinceInfo {
  id: number;
  name: string;
  code: string;
}

export interface CityInfo {
  id: number;
  provinceId: number;
  name: string;
  code: string;
}

export interface DistrictInfo {
  id: number;
  cityId: number;
  name: string;
  code: string;
}

// 公寓信息
export interface ApartmentItemVo {
  id: number;
  name: string;
  intro: string;
  districtId: number;
  districtName: string;
  address: string;
  nearMetroStation: string;
  photoUrl: string;
  minRent: number;
  labels: string[];
}

export interface ApartmentDetailVo {
  id: number;
  name: string;
  intro: string;
  districtId: number;
  districtName: string;
  cityId: number;
  cityName: string;
  provinceId: number;
  provinceName: string;
  address: string;
  nearMetroStation: string;
  phone: string;
  photoUrl: string;
  graphUrlList: string[];
  labels: LabelInfo[];
  facilities: FacilityInfo[];
  additionalFees: FeeValue[];
  minRent: number;
}

// 房间信息
export interface RoomItemVo {
  id: number;
  roomNumber: string;
  rent: number;
  apartmentId: number;
  apartmentName: string;
  photoUrl: string;
  isReleased: boolean;
  labels: string[];
}

export interface RoomDetailVo {
  id: number;
  roomNumber: string;
  rent: number;
  apartmentId: number;
  apartmentName: string;
  photoUrl: string;
  isReleased: boolean;
  graphUrlList: string[];
  labels: LabelInfo[];
  facilities: FacilityInfo[];
  paymentTypeList: PaymentType[];
  leaseTermList: LeaseTerm[];
  attributes: RoomAttrValue[];
}

export interface RoomQueryVo {
  apartmentId?: number;
  districtId?: number;
  provinceId?: number;
  cityId?: number;
  keyWord?: string;
}

export interface RoomAttrValue {
  attrName: string;
  attrValue: string;
}

// 标签和设施
export interface LabelInfo {
  id: number;
  name: string;
  icon: string;
  type: string;
}

export interface FacilityInfo {
  id: number;
  name: string;
  icon: string;
}

// 费用
export interface FeeValue {
  id: number;
  feeName: string;
  feeType: string;
  feePrice: number;
}

// 支付方式和租期
export interface PaymentType {
  id: number;
  name: string;
  payType: string;
}

export interface LeaseTerm {
  id: number;
  name: number;
  count: number;
  unit: string;
}

// 登录相关
export interface LoginVo {
  phone: string;
  code: string;
}

export interface UserInfoVo {
  id: number;
  phone: string;
  nickname: string;
  avatarUrl: string;
}

// 看房预约
export interface ViewAppointment {
  id?: number;
  userId?: number;
  apartmentId: number;
  appointmentTime: string;
  note?: string;
}

export interface AppointmentItemVo {
  id: number;
  apartmentId: number;
  apartmentName: string;
  apartmentPhotoUrl: string;
  appointmentTime: string;
  note: string;
  appointmentStatus: AppointmentStatus;
  provinceName: string;
  cityName: string;
  districtName: string;
  address: string;
}

export interface AppointmentDetailVo extends AppointmentItemVo {
  phone: string;
}

export enum AppointmentStatus {
  PENDING = '待确认',
  CONFIRMED = '已确认',
  CANCELLED = '已取消',
  COMPLETED = '已完成',
}

// 租约
export interface LeaseAgreement {
  id?: number;
  userId?: number;
  roomId: number;
  leaseStartDate: string;
  leaseEndDate: string;
  leaseStatus: LeaseStatus;
}

export interface AgreementItemVo {
  id: number;
  roomId: number;
  roomNumber: string;
  apartmentId: number;
  apartmentName: string;
  apartmentPhotoUrl: string;
  leaseStartDate: string;
  leaseEndDate: string;
  leaseStatus: LeaseStatus;
  paymentType: string;
  rent: number;
}

export interface AgreementDetailVo extends AgreementItemVo {
  phone: string;
  leaseTermName: string;
  graphUrlList: string[];
}

export enum LeaseStatus {
  SIGNING = '待签约',
  EXECUTE = '生效中',
  TERMINATION = '已终止',
  RENEWAL = '续约中',
}

// 浏览历史
export interface HistoryItemVo {
  id: number;
  apartmentId: number;
  apartmentName: string;
  apartmentPhotoUrl: string;
  roomId: number;
  roomNumber: string;
  rent: number;
  browsingTime: string;
}

// 分页结果
export interface PageResult<T> {
  records: T[];
  total: number;
  size: number;
  current: number;
  pages: number;
}

// API统一响应
export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}
