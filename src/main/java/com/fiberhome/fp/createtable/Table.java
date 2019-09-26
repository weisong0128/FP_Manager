package com.fiberhome.fp.createtable;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

public class Table {
	static String business_execl = "业务对接调查_v1.81_20190627_01.xls";
	static String FieldType_Conversion = "字段类型转化_v1.0_20190618.xls";
	static File execl_file = new File(business_execl);
	static File FieldType_Conversion_file = new File(FieldType_Conversion);

	private static  final Logger logger = LoggerFactory.getLogger(Table.class);
	private static  final int NUMBER_1=1;
	private static  final int NUMBER_2=2;
	private static  final int NUMBER_3=3;
	private static  final int NUMBER_4=4;
	private static  final int NUMBER_5=5;
	private static  final int NUMBER_6=6;
	private static  final int NUMBER_7=7;
	private static  final int NUMBER_8=8;
	private static  final int NUMBER_9=9;
	private static  final int NUMBER_10=10;
	private static  final int NUMBER_11=11;
	private static  final int NUMBER_30=30;
	private static  final int NUMBER_2000=2000;
	public Map<String,String> PublicMuilt(File file1, File file2,String path) throws BiffException, IOException {


		//获取项目地市和创建时间数据
		String pjLocation = file1.getName().split("_")[0];
		String pjName = file1.getName().split("_")[1];
		String createTime = file1.getName().split("_")[NUMBER_3];
		if (createTime.contains("xls")){
			createTime = createTime.substring(0,createTime.length()-NUMBER_4);
		}else if(createTime.contains("xlsx")){
			createTime = createTime.substring(0,createTime.length()-NUMBER_5);
		}
		Map<String,String> map = new HashMap<>();
		String fp_datatype = "";
		// 获取当前日期
		SimpleDateFormat date = new SimpleDateFormat("yyyyMMdd");
		String currentdate = date.format(new Date());

		// 写入文件
		String sqlFileName = file1.getName().split("\\.")[0]+"_v1.0_" + currentdate + ".sql";
		File file = new File(path + File.separator + sqlFileName);
		if (file.exists()) {
			file.delete();
		}

		/*FileWriter fWriter=null;
		FileWriter tablewrite=null;
		FileWriter tableSqlWrite=null;*/
		try (FileWriter fWriter = new FileWriter(path + File.separator + sqlFileName, true);){
			map.put("sqlName",sqlFileName);
			fWriter.write("--业务对接表结构设计" + "\r\n");
			InputStream tablefilename = new FileInputStream(file1.getAbsolutePath());
			InputStream fieldfilename = new FileInputStream(file2.getAbsoluteFile());
			// 业务对接表
			Workbook wb = Workbook.getWorkbook(tablefilename);
			// 字段类型转换
			Workbook field = Workbook.getWorkbook(fieldfilename);
			int sheet_number = wb.getNumberOfSheets();
			//公共变量
			String tablename="";
			// 循环每一个sheet表格
			for (int sheetnum = 0; sheetnum < sheet_number; sheetnum++) {
				Sheet sheet = wb.getSheet(sheetnum);

				if (sheet.getRows() == 0) {
					continue;
				}

				String sheetname = wb.getSheet(sheetnum).getName();
				//System.out.println("正在生成" + sheetname + "的建表脚本");
				logger.info("正在生成{}的建表脚本", sheetname);
				tablename = sheet.getCell(1, 0).getContents().trim().toUpperCase();
				String tabletype = sheet.getCell(1, 1).getContents().trim();
				String physicaltable = sheet.getCell(1, NUMBER_2).getContents().trim();
				// 自定义汇聚序号
				int zdynum = -1;
				// 存储序列号
				String definnum = "";
				Map<Integer, String> defmap = new HashMap<Integer, String>();
				// 临时存储汇聚列值
				Map<String, String> indexmap = new HashMap<String, String>();
				// 最终存储汇聚列值
				Map<String, String> defindexmap = new HashMap<String, String>();
				// 汇聚方式
				String mapping_store = "to_convtxt";
				// 自定义汇聚列名称
				List<String> stringlist = new ArrayList<String>();
				// 模糊检索汇聚列编号
				String likebh = "";
				// 模糊检索汇聚列
				String covlikename = "CONVLIKE";
				// 写入文件
				String txtFileName = tablename + ".txt";
				File tablefile = new File(path + File.separator + txtFileName);
				if (tablefile.exists()) {
					//System.err.println("已经存在");
					logger.error("已经存在");
					tablefile.delete();
				}
				File tableSqlFile = new File(path + File.separator + tablename + ".sql");
				if (tableSqlFile.exists()) {
					//System.err.println("已经存在");
					logger.error("已经存在");
					tableSqlFile.delete();
				}
				//为了入库每张表生成一个文件
				try (FileWriter tablewrite = new FileWriter(path + File.separator + txtFileName, true);
					 FileWriter tableSqlWrite = new FileWriter(path + File.separator + tablename + ".sql", true);) {
					map.put(txtFileName,txtFileName);
					tableSqlWrite.write( pjName+"\t");
					tableSqlWrite.write(pjLocation+"\t");
					tableSqlWrite.write(tablename+"\t");
					tableSqlWrite.write(createTime+"\t");
					// 拼接SQL--表头
					if ("物理表".equals(tabletype)) {
						fWriter.write("create table " + tablename + "(\r\n");
						tableSqlWrite.write("create table " + tablename + "( ");
					} else if ("映射表".equals(tabletype)) {
						fWriter.write("create mapping " + tablename + "(\r\n");
						tableSqlWrite.write("create mapping " + tablename + "( ");
					} else {
						//System.out.println("表设计类型输入有误！请检查.....");
						logger.error("表设计类型输入有误！请检查.....");
					}
					for (int m = 0; m < sheet.getRows() - NUMBER_10; m++) {
						String defname = tablename + "_CUSTOM";
						String deftrue = sheet.getCell(NUMBER_11, m + NUMBER_4).getContents().trim();
						if ("是".equals(deftrue) && !stringlist.contains(defname)) {
							stringlist.add(tablename + "_CUSTOM");
						}
					}
					// 模糊检索汇聚列
					for (int k = 0; k < sheet.getRows() - NUMBER_10; k++) {
						String liketrue = sheet.getCell(NUMBER_8, k + NUMBER_4).getContents().trim();
						String defconv = sheet.getCell(NUMBER_11, k + NUMBER_4).getContents().trim();
						if ("是".equals(liketrue) && "".equals(defconv) && !stringlist.contains(covlikename)) {
							stringlist.add(covlikename);
						}
					}
					// 统计可展示字段(独立的)
					int exhibitnum = 0;
					for (int i = 0; i < sheet.getRows() - NUMBER_10; i++) {
						String exhibition_true = sheet.getCell(NUMBER_3, i + NUMBER_4).getContents().trim();
						// 统计要展示的字段个数
						if ("是".equals(exhibition_true)) {
							exhibitnum++;
						}
					}
					// 空行统计
					int nullcount = 0;
					for (int i1 = 0; i1 < sheet.getRows() - NUMBER_10; i1++) {
						String fieldname1 = sheet.getCell(0, i1 + NUMBER_4).getContents().trim().toUpperCase();
						if (fieldname1.length() == 0) {
							nullcount++;
						}
					}
					/**
					 * 确定汇聚方式
					 */
					for (int mapping = 0; mapping < sheet.getRows() - NUMBER_10; mapping++) {
						// 全文检索：是否默认汇聚列创建索引
						// 可索引过滤
						String storeindex = sheet.getCell(NUMBER_4, mapping + NUMBER_4).getContents().trim();
						// 可模糊检索
						String storelike = sheet.getCell(NUMBER_8, mapping + NUMBER_4).getContents().trim();
						if ("是".equals(storeindex) || "是".equals(storelike)) {
							mapping_store = mapping_store + "_convindex";
							break;
						}
					}
					for (int row = 0; row < sheet.getRows() - NUMBER_10; row++) {
						// 字段名:全部转化成大写
						String fieldname = sheet.getCell(0, row + NUMBER_4).getContents().trim().toUpperCase();
						// 业务对接表中的字段类型
						String datatypetemp = sheet.getCell(NUMBER_1, row + NUMBER_4).getContents().trim().toLowerCase();
						String datatype = "";
						//字段描述
						String fielddescript=sheet.getCell(NUMBER_2, row + NUMBER_4).getContents().trim();
						// 可视化展示
						String exhibition = sheet.getCell(NUMBER_3, row + NUMBER_4).getContents().trim();
						// 可索引过滤
						String index = sheet.getCell(NUMBER_4, row + NUMBER_4).getContents().trim();
						// 可排序
						String order = sheet.getCell(NUMBER_5, row + NUMBER_4).getContents().trim();
						// 可聚合
						String count = sheet.getCell(NUMBER_6, row + NUMBER_4).getContents().trim();
						// 可分组
						String group = sheet.getCell(NUMBER_7, row + NUMBER_4).getContents().trim();
						// 可模糊检索
						String like = sheet.getCell(NUMBER_8, row + NUMBER_4).getContents().trim();
						// 多值列
						String datam = sheet.getCell(NUMBER_9, row + NUMBER_4).getContents().trim();
						// 字段是否超长
						String datalong = sheet.getCell(NUMBER_10, row + NUMBER_4).getContents().trim();
						// 全文检索1
						String definconvtxt = sheet.getCell(NUMBER_11, row + NUMBER_4).getContents().trim();
						// 自定义汇聚名称
						String definconvtxtname = "";

						if ("是".equals(definconvtxt)) {
							definconvtxtname = tablename + "_CUSTOM";
						}
						// 统计自定义汇聚列序号
						zdynum++;
						// System.out.println(stringarray[1]);
						if ("是".equals(definconvtxt)) {
							defmap.put(zdynum, definconvtxtname);
						}
						// 模糊检索但是不存在与全文检索字段中
						if ("是".equals(like) && "".equals(definconvtxt)) {
							// likebh=likebh+","+Integer.toString(zdynum);
							defmap.put(zdynum, covlikename);
						}

						// 初步处理处理字段类型转换
						if (datatypetemp.contains("(")) {
							datatype = datatypetemp.substring(0, datatypetemp.indexOf("(")).toLowerCase();
						} else {
							datatype = datatypetemp;
						}
						if (datatype.trim().length() != 0) {
							//业务调查表元数据入库
							tablewrite.write(pjName+"\t"+pjLocation+"\t"+tablename+"\t"+tabletype+"\t"+physicaltable+"\t"
									+fieldname+"\t"+datatypetemp+"\t"+fielddescript+"\t"+exhibition+"\t"+index
									+"\t"+order+"\t"+count+"\t"+group+"\t"+like+"\t"+datam+"\t"+datalong+"\t"+definconvtxt+"\t"+System.currentTimeMillis()+"\r");

							// 获取字段类型转换
							Sheet fieldsheet = field.getSheet(0);
							String olddatatype = "";
							String newdatatype = "";
							for (int fieldrow = 1; fieldrow < fieldsheet.getRows(); fieldrow++) {
								olddatatype = fieldsheet.getCell(0, fieldrow).getContents().trim().toLowerCase();
								newdatatype = fieldsheet.getCell(1, fieldrow).getContents().trim();
								if(datatype.equals(olddatatype) && "是".equals(datalong)) {
									newdatatype = "wildcard4";
								}else {
									newdatatype = newdatatype;
									if (olddatatype.contains("number") && (datatypetemp.indexOf(",")>=0 || datatypetemp.indexOf("，")>=0)){
										newdatatype = "tfloat";
									}
									fp_datatype = "y_" + newdatatype + "_";
								}

							}
							// 根据不同的建表类型进行处理
							if ("物理表".equals(tabletype)) {
								if ("是".equals(index)) {
									fp_datatype = fp_datatype + "i";
								} else if ("".equals(index) && "".equals(exhibition) && "".equals(order) && "".equals(count)
										&& "".equals(group) && "".equals(datam)) {
									fp_datatype = fp_datatype + "n";
								}
								// 排序、分组、统计都必须加上索引
								if (("是".equals(order) || "是".equals(count) || "是".equals(group)) && "".equals(index)) {
									fp_datatype = fp_datatype + "id";
								} else if (("是".equals(order) || "是".equals(count) || "是".equals(group))
										&& "是".equals(index)) {
									fp_datatype = fp_datatype + "d";
								} else if ("是".equals(exhibition) && exhibitnum > NUMBER_8 && "".equals(order) && "".equals(count)
										&& "".equals(group) && "".equals(datam)) {
									fp_datatype = fp_datatype + "s";
								} else if ("是".equals(exhibition) && exhibitnum > NUMBER_8 && "".equals(order) && "".equals(count)
										&& "".equals(group) && "是".equals(datam)) {
									fp_datatype = fp_datatype + "d";
								}
								if ("是".equals(datam)) {
									fp_datatype = fp_datatype + "m";
								}

							} else if ("映射表".equals(tabletype)) {
								// 没有需求就既不存储数据也不存储索引
								if ("".equals(index) && "".equals(exhibition) && "".equals(order) && "".equals(count)
										&& "".equals(group) && "".equals(datam)) {
									fp_datatype = fp_datatype + "n";
								} else {
									fp_datatype = fp_datatype + "i";
								}

								if ("是".equals(order) || "是".equals(count) || "是".equals(group)) {
									fp_datatype = fp_datatype + "d";
								}
								if ("是".equals(datam)) {
									fp_datatype = fp_datatype + "m";
								}
							} else {
								//System.err.println("对于" + tablename + "业务表，您给出的建表类型，宝宝不认识，请检查下好吗");
								logger.error("对于{}业务表，您给出的建表类型，宝宝不认识，请检查下好吗",tablename);
							}
							int field_len = fieldname.length();
							int type_len = fp_datatype.length();
							// 调整格式空白(字段后)
							String spacestring = "";
							for (int s = 0; s < (NUMBER_30 - field_len); s++) {
								spacestring = spacestring + " ";
							}
							// 调整格式空白(字段类型后)
							String typespacestring = "";
							for (int s1 = 0; s1 < (NUMBER_30 - type_len); s1++) {
								typespacestring = typespacestring + " ";
							}

							// 拼接SQL--字段名
							if ("物理表".equals(tabletype)) {

								if (row == sheet.getRows() - NUMBER_11 - nullcount
										&& (stringlist.size() == 0 || stringlist == null)) {

									fWriter.write(fieldname + spacestring + fp_datatype);
									tableSqlWrite.write(fieldname + spacestring + fp_datatype);
									//tablewrite.write(fieldname + spacestring + fp_datatype);
								} else {
									fWriter.write(fieldname + spacestring + fp_datatype + ",\r\n");
									tableSqlWrite.write(fieldname + spacestring + fp_datatype + ", ");
									//tablewrite.write(fieldname + spacestring + fp_datatype + ",\r\n");
								}

							} else if ("映射表".equals(tabletype)) {
								/**
								 * 默认是进行默认汇聚，如果有选择不默认汇聚，那么就控制映射
								 */
								// 最后一个形式
								if (row == sheet.getRows() - NUMBER_11 - nullcount
										&& (stringlist.size() == 0 || stringlist == null)) {
									if ("是".equals(order) || "是".equals(group) || "是".equals(count)) {
										fWriter.write(fieldname + spacestring + fp_datatype + typespacestring
												+ "copy@to_field_convtxt_convindex" + "  " + "physical@" + fieldname + "_"
												+ fp_datatype + "\r\n");
										tableSqlWrite.write(fieldname + spacestring + fp_datatype + typespacestring
												+ "copy@to_field_convtxt_convindex" + "  " + "physical@" + fieldname + "_"
												+ fp_datatype + " ");
									/*
									 * tablewrite.write(fieldname + spacestring + fp_datatype + typespacestring
											+ "copy@to_field_convtxt_convindex" + "  " + "physical@" + fieldname + "_"
											+ fp_datatype + "\r\n");*/
									} else if ("".equals(exhibition) && "".equals(index) && "".equals(order)
											&& "".equals(group) && "".equals(count) && "".equals(like)) {
										fWriter.write(
												fieldname + spacestring + fp_datatype + typespacestring + "copy@to_field"
														+ "  " + "physical@" + fieldname + "_" + fp_datatype + "\r\n");
										tableSqlWrite.write(
												fieldname + spacestring + fp_datatype + typespacestring + "copy@to_field"
														+ "  " + "physical@" + fieldname + "_" + fp_datatype + " ");
									} else {
										fWriter.write(fieldname + spacestring + fp_datatype + typespacestring + "physical@"
												+ fieldname + "_" + fp_datatype + "\r\n");
										tableSqlWrite.write(fieldname + spacestring + fp_datatype + typespacestring + "physical@"
												+ fieldname + "_" + fp_datatype + " ");
									}
									// 不是最后一个元素
								} else {
									if ("是".equals(order) || "是".equals(group) || "是".equals(count)) {
										fWriter.write(fieldname + spacestring + fp_datatype + typespacestring
												+ "copy@to_field_convtxt_convindex" + "  " + "physical@" + fieldname + "_"
												+ fp_datatype + ",\r\n");
										tableSqlWrite.write(fieldname + spacestring + fp_datatype + typespacestring
												+ "copy@to_field_convtxt_convindex" + "  " + "physical@" + fieldname + "_"
												+ fp_datatype + ", ");
									} else if ("".equals(exhibition) && "".equals(index) && "".equals(order)
											&& "".equals(group) && "".equals(count) && "".equals(like)) {
										fWriter.write(
												fieldname + spacestring + fp_datatype + typespacestring + "copy@to_field"
														+ "  " + "physical@" + fieldname + "_" + fp_datatype + ",\r\n");
										tableSqlWrite.write(
												fieldname + spacestring + fp_datatype + typespacestring + "copy@to_field"
														+ "  " + "physical@" + fieldname + "_" + fp_datatype + ", ");
									} else {
										fWriter.write(fieldname + spacestring + fp_datatype + typespacestring + "physical@"
												+ fieldname + "_" + fp_datatype + ",\r\n");
										tableSqlWrite.write(fieldname + spacestring + fp_datatype + typespacestring + "physical@"
												+ fieldname + "_" + fp_datatype + ", ");
									}
								}

							}

						}

					}
					// 自定义汇聚列的处理
					TreeSet<String> keys = new TreeSet<String>();
					// System.out.println(stringlist.size());
					for (int jj = 0; jj < stringlist.size(); jj++) {
						for (Entry<Integer, String> entry : defmap.entrySet()) {
							if (entry.getValue().equals(stringlist.get(jj))) {
								keys.add(entry.getKey().toString());
							}
						}
						indexmap.put(stringlist.get(jj), keys.toString());
						keys.clear();
					}
					int biaojie = 0;
					//likebh = "[" + likebh + "]";
					for (Entry<String, String> entry1 : indexmap.entrySet()) {
						// 作用：（1）用于连续的数字使用~分割 序列合并
						String teString = entry1.getValue().replace("[", "").replace("]", "").replace(" ", "").trim();

						String[] teString1 = teString.split(",");
						int[] noNum = new int[teString1.length];
						for (int wwww = 0; wwww < noNum.length; wwww++) {
							noNum[wwww] = Integer.parseInt(teString1[wwww]);
						}
						Arrays.sort(noNum);
						int state = 0;
						String result = "";
						for (int i = 0; i < noNum.length; i++) {
							if (i == noNum.length - 1)
								state = NUMBER_2;
							if (state == 0) {
								if (noNum[i + 1] == noNum[i] + 1) {
									result += Integer.toString(noNum[i]);
									result += "~";
									state = 1;
								} else {
									result += Integer.toString(noNum[i]);
									result += ",";
								}
							} else if (state == 1) {
								if (noNum[i + 1] != noNum[i] + 1) {
									result += Integer.toString(noNum[i]);
									result += ",";
									state = 0;
								}
							} else {
								result += Integer.toString(noNum[i]);
							}
						}

						// 自定义汇聚列字段类型
						String defdatatype = "y_wildcard4_isp";
						int defdata_len = defdatatype.length();
						String defdataspace = "";
						for (int s111 = 0; s111 < (NUMBER_30 - defdata_len); s111++) {
							defdataspace = defdataspace + " ";
						}
						// 定义空白符(自定义汇聚列)
						int def_len = entry1.getKey().length();
						String defspace = "";
						for (int s11 = 0; s11 < (NUMBER_30 - def_len); s11++) {
							defspace = defspace + " ";
						}
						// 用于汇聚列计数
						biaojie++;
						if ("物理表".equals(tabletype)) {
							if (biaojie == stringlist.size()) {
								fWriter.write(entry1.getKey() + defspace + defdatatype + "\r\n");
								tableSqlWrite.write(entry1.getKey() + defspace + defdatatype + " ");
							} else {
								fWriter.write(entry1.getKey() + defspace + defdatatype + ",\r\n");
								tableSqlWrite.write(entry1.getKey() + defspace + defdatatype + ", ");
							}

						} else if ("映射表".equals(tabletype)) {
							if (biaojie == stringlist.size()) {
								fWriter.write(entry1.getKey() + defspace + defdatatype + defdataspace
										+ "copy@to_field  physical@" + entry1.getKey() + "_" + defdatatype + "\r\n");
								tableSqlWrite.write(entry1.getKey() + defspace + defdatatype + defdataspace
										+ "copy@to_field  physical@" + entry1.getKey() + "_" + defdatatype + " ");
							} else {
								fWriter.write(entry1.getKey() + defspace + defdatatype + defdataspace
										+ "copy@to_field  physical@" + entry1.getKey() + "_" + defdatatype + ",\r\n");
								tableSqlWrite.write(entry1.getKey() + defspace + defdatatype + defdataspace
										+ "copy@to_field  physical@" + entry1.getKey() + "_" + defdatatype + ", ");
							}

						}

						defindexmap.put(entry1.getKey(), result);
					}

					// 拼接SQL--尾部

					if ("物理表".equals(tabletype)) {
						// 没有自定义汇聚
						if (stringlist == null || stringlist.size() == 0) {
							fWriter.write("\r\n);\r\n\r\n");
							tableSqlWrite.write(" );  ");
						} else {
							fWriter.write(")" + "\r\n" + "tableproperties" + "\r\n" + "(" + "\r\n");
							tableSqlWrite.write(")" + " " + "tableproperties" + " " + "(" + " ");
							for (int qqq = 0; qqq < stringlist.size(); qqq++) {
								if (qqq == stringlist.size() - 1) {
									fWriter.write("copyfield='rangeof@" + defindexmap.get(stringlist.get(qqq)) + " dest@"
											+ stringlist.get(qqq) + " withfield@true'");
									tableSqlWrite.write("copyfield='rangeof@" + defindexmap.get(stringlist.get(qqq)) + " dest@"
											+ stringlist.get(qqq) + " withfield@true'");
								} else {
									fWriter.write("copyfield='rangeof@" + defindexmap.get(stringlist.get(qqq)) + " dest@"
											+ stringlist.get(qqq) + " withfield@true',\r\n");
									tableSqlWrite.write("copyfield='rangeof@" + defindexmap.get(stringlist.get(qqq)) + " dest@"
											+ stringlist.get(qqq) + " withfield@true', ");
								}

							}
							fWriter.write("\r\n);\r\n\r\n");
							tableSqlWrite.write(" );  ");
						}

					} else if ("映射表".equals(tabletype)) {
						fWriter.write(")" + "\r\n" + "tableproperties" + "\r\n" + "(" + "\r\n");
						tableSqlWrite.write(")" + " " + "tableproperties" + " " + "(" + " ");
						fWriter.write("mapping_physical_table='" + physicaltable.toUpperCase() + "',\r\n");
						tableSqlWrite.write("mapping_physical_table='" + physicaltable.toUpperCase() + "', ");
						if (stringlist == null || stringlist.size() == 0) {
							fWriter.write("mapping_store_config='" + mapping_store + "'");
							tableSqlWrite.write("mapping_store_config='" + mapping_store + "'");
						} else {
							fWriter.write("mapping_store_config='" + mapping_store + "',\r\n");
							tableSqlWrite.write("mapping_store_config='" + mapping_store + "', ");
							for (int qqq = 0; qqq < stringlist.size(); qqq++) {
								if (qqq == stringlist.size() - 1) {
									fWriter.write("copyfield='rangeof@" + defindexmap.get(stringlist.get(qqq)) + " dest@"
											+ stringlist.get(qqq) + " withfield@true'");
									tableSqlWrite.write("copyfield='rangeof@" + defindexmap.get(stringlist.get(qqq)) + " dest@"
											+ stringlist.get(qqq) + " withfield@true'");
								} else {
									fWriter.write("copyfield='rangeof@" + defindexmap.get(stringlist.get(qqq)) + " dest@"
											+ stringlist.get(qqq) + " withfield@true',\r\n");
									tableSqlWrite.write("copyfield='rangeof@" + defindexmap.get(stringlist.get(qqq)) + " dest@"
											+ stringlist.get(qqq) + " withfield@true', ");
								}

							}
						}
						fWriter.write("\r\n);\r\n\r\n");
						tableSqlWrite.write(" );  ");
					}
					fWriter.flush();
					tableSqlWrite.flush();
					tablewrite.flush();
					defindexmap.clear();
					// 为了进度条效果，每次执行后，休眠2s
					try {
						Thread.sleep(NUMBER_2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return map;
	}

	// 定义休眠类
/*	class Runner1 implements Runnable {
		public void run() {
			try {
				Thread.sleep(20000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}*/

}
