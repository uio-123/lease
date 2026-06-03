import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Phone, Lock } from 'lucide-react';
import { Card, Form, Input, Button, Toast } from 'antd-mobile';
import { getCode, login } from '@/api';
import { useAuthStore } from '@/store/auth';
import './LoginPage.css';

export default function LoginPage() {
  const [phone, setPhone] = useState('');
  const [code, setCode] = useState('');
  const [countdown, setCountdown] = useState(0);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const { setToken } = useAuthStore();

  const handleGetCode = async () => {
    if (!/^1[3-9]\d{9}$/.test(phone)) {
      Toast.show('请输入正确的手机号');
      return;
    }
    try {
      await getCode(phone);
      Toast.show('验证码已发送');
      setCountdown(60);
      const timer = setInterval(() => {
        setCountdown((prev) => {
          if (prev <= 1) {
            clearInterval(timer);
            return 0;
          }
          return prev - 1;
        });
      }, 1000);
    } catch {
      Toast.show('发送失败');
    }
  };

  const handleLogin = async () => {
    if (!/^1[3-9]\d{9}$/.test(phone)) {
      Toast.show('请输入正确的手机号');
      return;
    }
    if (!/^\d{4,6}$/.test(code)) {
      Toast.show('请输入4-6位验证码');
      return;
    }
    setLoading(true);
    try {
      const res = await login({ phone, code });
      setToken(res.data);
      Toast.show('登录成功');
      navigate('/');
    } catch {
      Toast.show('登录失败');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-container">
      <div className="login-header">
        <h1>公寓租赁</h1>
        <p>找到理想的居住空间</p>
      </div>
      <Card className="login-card">
        <Form layout="vertical">
          <Form.Item label="手机号">
            <div className="input-with-icon">
              <Phone className="input-icon" />
              <Input
                type="phone"
                placeholder="请输入手机号"
                value={phone}
                onChange={(val) => setPhone(val)}
                clearable
              />
            </div>
          </Form.Item>
          <Form.Item label="验证码">
            <div className="code-input">
              <div className="input-with-icon">
                <Lock className="input-icon" />
                <Input
                  type="number"
                  placeholder="请输入验证码"
                  value={code}
                  onChange={(val) => setCode(val)}
                  clearable
                />
              </div>
              <Button
                size="small"
                onClick={handleGetCode}
                disabled={countdown > 0}
              >
                {countdown > 0 ? `${countdown}s` : '获取验证码'}
              </Button>
            </div>
          </Form.Item>
        </Form>
        <Button
          block
          color="primary"
          size="large"
          loading={loading}
          onClick={handleLogin}
          className="login-btn"
        >
          登录
        </Button>
      </Card>
    </div>
  );
}
