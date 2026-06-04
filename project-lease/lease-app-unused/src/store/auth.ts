import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import type { UserInfoVo } from '@/types';

interface AuthState {
  token: string | null;
  userInfo: UserInfoVo | null;
  setToken: (token: string) => void;
  setUserInfo: (userInfo: UserInfoVo) => void;
  logout: () => void;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      token: null,
      userInfo: null,
      setToken: (token) => set({ token }),
      setUserInfo: (userInfo) => set({ userInfo }),
      logout: () => set({ token: null, userInfo: null }),
    }),
    {
      name: 'auth-storage',
    }
  )
);
