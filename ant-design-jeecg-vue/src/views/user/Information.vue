<template>
  <div class="main user-layout-register">
    <h3><span>信息采集/information collection</span></h3>
    <a-form ref="formRegister" :autoFormCreate="(form)=>{this.form = form}" id="formRegister">
      <a-form-item>
        <!--fieldDecoratorId="userName"
        :fieldDecoratorOptions="{rules: [{ required: true,  message: '请输入姓名/username' }], validateTrigger: ['change', 'blur']}"-->
        <a-input size="large" type="text" placeholder="姓名/username"></a-input>
      </a-form-item>

      <a-form-item>
        <!--fieldDecoratorId="userSex"
        :fieldDecoratorOptions="{rules: [{ required: true,  message: '请选择性别/sex' }], validateTrigger: ['change', 'blur']}"-->

        <span>请选择性别/sex</span>
        <a-radio-group v-decorator="['menuType',{'initialValue':1}]" >
          <a-radio :value="1" class="radio-userSex-man">男</a-radio>
          <a-radio :value="2" class="radio-userSex-woman">女</a-radio>
        </a-radio-group>
      </a-form-item>

      <a-form-item>
        <!--fieldDecoratorId="userBirthday"
        :fieldDecoratorOptions="{rules: [{ required: true,  message: '请选择出生日期/birthday' }], validateTrigger: ['change', 'blur']}" -->
        <a-date-picker  style="width: 100%" placeholder="请选择出生日期/birthday" v-decorator="[ 'birthday', {}]" />
      </a-form-item>

      <a-form-item>
        <!--fieldDecoratorId="userEducational"
        :fieldDecoratorOptions="{rules: [{ required: true,  message: '请选择当前学历/educational background' }], validateTrigger: ['change', 'blur']}"-->
        <a-select
          style="width: 100%"
          v-decorator="['userEducational', {}]" placeholder="请选择当前学历/educational background">
          <a-select-option value="">请选择性别</a-select-option>
          <a-select-option value="1">博士研究生</a-select-option>
          <a-select-option value="2">硕士研究生</a-select-option>
          <a-select-option value="3">本科</a-select-option>
          <a-select-option value="4">高职</a-select-option>
          <a-select-option value="5">中专</a-select-option>
          <a-select-option value="6">高级中学</a-select-option>
          <a-select-option value="7">初级中学</a-select-option>
          <a-select-option value="8">小学</a-select-option>
        </a-select>
      </a-form-item>

      <a-form-item>
        <a-button
          size="large"
          type="primary"
          htmlType="submit"
          class="register-button"
          :loading="registerBtn"
          @click.stop.prevent="handleSubmit"
          :disabled="registerBtn">确定
        </a-button>
        <router-link class="login" :to="{ name: 'login' }">使用已有账户检测</router-link>
      </a-form-item>
    </a-form>
  </div>
</template>

<script>
  import { mixinDevice } from '@/utils/mixin.js'
  import { getSmsCaptcha } from '@/api/login'
  import AFormItem from 'ant-design-vue/es/form/FormItem'
  import AInputGroup from 'ant-design-vue/es/input/Group'
  import ARadioGroup from 'ant-design-vue/es/radio/Group'
  import ARadioButton from 'ant-design-vue/es/radio/RadioButton'
  const levelNames = {
    0: '低',
    1: '低',
    2: '中',
    3: '强'
  }
  const levelClass = {
    0: 'error',
    1: 'error',
    2: 'warning',
    3: 'success'
  }
  const levelColor = {
    0: '#ff0000',
    1: '#ff0000',
    2: '#ff7e05',
    3: '#52c41a',
  }
  export default {
    name: "Register",
    components: {
      ARadioButton,
      ARadioGroup,
      AInputGroup,
      AFormItem
    },
    mixins: [mixinDevice],
    data() {
      return {
        form: null,
        state: {
          time: 60,
          smsSendBtn: false,
          passwordLevel: 0,
          passwordLevelChecked: false,
          percent: 10,
          progressColor: '#FF0000'
        },
        registerBtn: false
      }
    },
    computed: {
      passwordLevelClass () {
        return levelClass[this.state.passwordLevel]
      },
      passwordLevelName () {
        return levelNames[this.state.passwordLevel]
      },
      passwordLevelColor () {
        return levelColor[this.state.passwordLevel]
      }
    },
    methods: {
      handlePasswordLevel (rule, value, callback) {
        let level = 0
        // 判断这个字符串中有没有数字
        if (/[0-9]/.test(value)) {
          level++
        }
        // 判断字符串中有没有字母
        if (/[a-zA-Z]/.test(value)) {
          level++
        }
        // 判断字符串中有没有特殊符号
        if (/[^0-9a-zA-Z_]/.test(value)) {
          level++
        }
        this.state.passwordLevel = level
        this.state.percent = level * 30
        if (level >= 2) {
          if (level >= 3) {
            this.state.percent = 100
          }
          callback()
        } else {
          if (level === 0) {
            this.state.percent = 10
          }
          callback(new Error('密码强度不够'))
        }
      },
      handlePasswordCheck (rule, value, callback) {
        let password = this.form.getFieldValue('password')
        console.log('value', value)
        if (value === undefined) {
          callback(new Error('请输入密码'))
        }
        if (value && password && value.trim() !== password.trim()) {
          callback(new Error('两次密码不一致'))
        }
        callback()
      },
      handlePhoneCheck (rule, value, callback) {
        console.log('handlePhoneCheck, rule:', rule)
        console.log('handlePhoneCheck, value', value)
        console.log('handlePhoneCheck, callback', callback)
        callback()
      },
      handlePasswordInputClick () {
        if (!this.isMobile()) {
          this.state.passwordLevelChecked = true
          return;
        }
        this.state.passwordLevelChecked = false
      },
      handleSubmit() {
        this.form.validateFields((err, values) => {
          if (!err) {
            this.$router.push({ name: 'helloword', params: {...values} })
          }
        })
      },
      getCaptcha(e) {
        e.preventDefault()
        let that = this
        this.form.validateFields(['mobile'], {force: true},
          (err, values) => {
            if (!err) {
              this.state.smsSendBtn = true;
              let interval = window.setInterval(() => {
                if (that.state.time-- <= 0) {
                  that.state.time = 60;
                  that.state.smsSendBtn = false;
                  window.clearInterval(interval);
                }
              }, 1000);
              const hide = this.$message.loading('验证码发送中..', 0);
              getSmsCaptcha({mobile: values.mobile}).then(res => {
                setTimeout(hide, 2500);
                this.$notification['success']({
                  message: '提示',
                  description: '验证码获取成功，您的验证码为：' + res.result.captcha,
                  duration: 8
                })
              }).catch(err => {
                setTimeout(hide, 1);
                clearInterval(interval);
                that.state.time = 60;
                that.state.smsSendBtn = false;
                this.requestFailed(err);
              });
            }
          }
        );
      },
      requestFailed(err) {
        this.$notification['error']({
          message: '错误',
          description: ((err.response || {}).data || {}).message || "请求出现错误，请稍后再试",
          duration: 4,
        });
        this.registerBtn = false;
      },
    },
    watch: {
      'state.passwordLevel' (val) {
        console.log(val)
      }
    }
  }
</script>
<style lang="scss">
  .user-register {
    &.error {
      color: #ff0000;
    }
    &.warning {
      color: #ff7e05;
    }
    &.success {
      color: #52c41a;
    }
  }
  .user-layout-register {
    .ant-input-group-addon:first-child {
      background-color: #fff;
    }
  }
</style>
<style lang="scss" scoped>
  .user-layout-register {
    & > h3 {
      font-size: 16px;
      margin-bottom: 20px;
    }
    .getCaptcha {
      display: block;
      width: 100%;
      height: 40px;
    }
    .register-button {
      width: 50%;
    }
    .radio-userSex-man{
      left: 40%;
    }
    .radio-userSex-woman{
      left: 100%;
    }
    .login {
      float: right;
      line-height: 40px;
    }
  }
</style>