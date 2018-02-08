#include <linux/init.h>
#include <linux/module.h>

#include <linux/kernel.h>
#include <linux/fs.h>
#include <mach/gpio.h>
#include <plat/gpio-cfg.h>
#include <linux/miscdevice.h>
#include <linux/platform_device.h>
//#include <mach/gpio-bank.h>
#include <mach/regs-gpio.h>
#include <asm/io.h>
#include <linux/regulator/consumer.h>
//#include "gps.h"
#include <linux/delay.h>

#include <media/ov5640.h>

#define LEDS_DEBUG
#ifdef LEDS_DEBUG
#define DPRINTK(x...) printk("LEDS_CTL DEBUG:" x)
#else
#define DPRINTK(x...)
#endif

#define DRIVER_NAME "leds"


#if  defined(CONFIG_CPU_TYPE_SCP_ELITE) || defined(CONFIG_CPU_TYPE_POP_ELITE) || defined(CONFIG_CPU_TYPE_POP2G_ELITE)
static int led_gpios[] = {
	EXYNOS4_GPL2(0),
	EXYNOS4_GPK1(1),
};

#elif defined(CONFIG_CPU_TYPE_SCP_SUPPER) || defined(CONFIG_CPU_TYPE_POP_SUPPER) || defined(CONFIG_CPU_TYPE_POP2G_SUPPER)


static int led_gpios[] = {
#if defined(CONFIG_MTK_COMBO_COMM) || defined(CONFIG_MTK_COMBO_COMM_MODULE)
	EXYNOS4_GPC0(2),
#else
	EXYNOS4_GPX2(5),
#endif
	EXYNOS4_GPX0(1),
};


#endif
#define LED_NUM		ARRAY_SIZE(led_gpios)

int leds_open(struct inode *inode,struct file *filp)
{
	DPRINTK("Device Opened Success!\n");
	return nonseekable_open(inode,filp);
}

int leds_release(struct inode *inode,struct file *filp)
{
	DPRINTK("Device Closed Success!\n");
	return 0;
}

int leds_pm(bool enable)
{
	int ret = 0;
	printk("debug: LEDS PM return %d\r\n" , ret);
	return ret;
};
//cmd 信息标志标志  arg 操作
long leds_ioctl(struct file *filp,unsigned int cmd,unsigned long arg)
{
	printk("cmd=%x,arg=%x\n",cmd,arg);
	switch(arg)
	{
		//工作模式：
	
		//融合
		case 0:
			ov5640_i2c_fpga(0,0);
			break;
		//可见
		case 1:
			ov5640_i2c_fpga(0,1);
			break;
		//紫外
		case 2:
			ov5640_i2c_fpga(0,2);
			break;
					
	//增益设置
		//增益+
		case 10:
			ov5640_i2c_fpga(1,0);
			break;
		//增益-
		case 11:
			ov5640_i2c_fpga(1,1);
			break;
		//调焦设置
		//自动
		case 20:
			ov5640_i2c_fpga(2,0);
			break;
		//手动+
		case 21:
			ov5640_i2c_fpga(2,1);
			break;
		//手动-
		case 22:
			ov5640_i2c_fpga(2,2);
			break;
		default:
			return -EINVAL;
	}

	return 0;
}

static struct file_operations leds_ops = {
	.owner 	= THIS_MODULE,
	.open 	= leds_open,
	.release= leds_release,
	.unlocked_ioctl 	= leds_ioctl,
};

static struct miscdevice leds_dev = {
	.minor	= MISC_DYNAMIC_MINOR,
	.fops	= &leds_ops,
	.name	= "leds",
};


static int leds_probe(struct platform_device *pdev)
{
	int ret, i;
	char *banner = "leds Initialize\n";

	printk(banner);

	for(i=0; i<LED_NUM; i++)
	{
		ret = gpio_request(led_gpios[i], "LED");
		if (ret) {
			printk("%s: request GPIO %d for LED failed, ret = %d\n", DRIVER_NAME,
					led_gpios[i], ret);
			return ret;
		}

		s3c_gpio_cfgpin(led_gpios[i], S3C_GPIO_OUTPUT);
		gpio_set_value(led_gpios[i], 1);
	}

	ret = misc_register(&leds_dev);
	if(ret<0)
	{
		printk("leds:register device failed!\n");
		goto exit;
	}

	return 0;

exit:
	misc_deregister(&leds_dev);
	return ret;
}

static int leds_remove (struct platform_device *pdev)
{
	misc_deregister(&leds_dev);	

	return 0;
}

static int leds_suspend (struct platform_device *pdev, pm_message_t state)
{
	DPRINTK("leds suspend:power off!\n");
	return 0;
}

static int leds_resume (struct platform_device *pdev)
{
	DPRINTK("leds resume:power on!\n");
	return 0;
}

static struct platform_driver leds_driver = {
	.probe = leds_probe,
	.remove = leds_remove,
	.suspend = leds_suspend,
	.resume = leds_resume,
	.driver = {
		.name = DRIVER_NAME,
		.owner = THIS_MODULE,
	},
};

static void __exit leds_exit(void)
{
	platform_driver_unregister(&leds_driver);
}

static int __init leds_init(void)
{
	return platform_driver_register(&leds_driver);
}

module_init(leds_init);
module_exit(leds_exit);

MODULE_LICENSE("Dual BSD/GPL");
