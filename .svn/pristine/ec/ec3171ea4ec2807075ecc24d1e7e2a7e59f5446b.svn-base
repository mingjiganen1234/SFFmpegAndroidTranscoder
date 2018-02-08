#include <linux/init.h>
#include <linux/module.h>
#include <linux/i2c.h>
#include <linux/platform_device.h>
#include <linux/delay.h>

#include <linux/slab.h>
#include <linux/kernel.h>

static const struct i2c_device_id FPGA_id[] = {
	{ "FPGA", 70 },
	{ }
};
static struct i2c_driver FPGA_i2c_driver = {
	.driver = {
		.name = "FPGA",
	},
	.probe    = FPGA_probe,
	.remove   = FPGA_remove,
	.id_table = FPGA_id,
};

/*
 * module function
 */
static int FPGA_probe(struct i2c_client *client,
			const struct i2c_device_id *did)
{
	printk("FPGA probe cycl" );	
	return 0;
}
static int FPGA_remove(struct i2c_client *client)
{
	printk("FPGA remove cycl" );	
	return 0;
}
				  
static int __init FPGA_module_init(void)
{
    
	printk("FPGA init cycl" );

	return i2c_add_driver(&FPGA_i2c_driver);
}

static void __exit FPGA_module_exit(void)
{
	printk("FPGA exit cycl" );
	i2c_del_driver(&FPGA_i2c_driver);
}

module_init(FPGA_module_init);
module_exit(FPGA_module_exit);

MODULE_DESCRIPTION("i2c driver for FPGA");
MODULE_AUTHOR("cycl ");
MODULE_LICENSE("GPL v2");
