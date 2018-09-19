package zhrk.disaster;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

import com.googlecode.javacv.cpp.opencv_highgui;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import zhrk.common.model.QxCamarea;
import zhrk.common.model.QxDisResult;
import zhrk.common.model.QxDisUser;
import zhrk.common.model.QxDisaster;
import zhrk.utils.FileUtil;
import zhrk.utils.MapUtils;
import zhrk.utils.baidu.FaceMatch;
import zhrk.utils.waston.AnalytisPicUtil;
import zhrk.utils.waston.ClassScorePo;
import zhrk.utils.waston.VideoToImgUtils;

public class DisasterService {

	public static final DisasterService me = new DisasterService();
	private QxDisaster dao = new QxDisaster().dao();
	private QxCamarea caDao = new QxCamarea().dao();

	/**
	 * 分页
	 * 
	 * @param pager
	 * @return
	 */
	public Page<Record> paginate(Integer pageNum) {
		return Db.paginate(pageNum, 10, "select id,dname,code,address,content,opentime ",
				"from qx_disaster ORDER BY id DESC");
	}

	/**
	 * 根据id查找灾难信息
	 * 
	 * @param id
	 * @return
	 */
	public QxDisaster findById(Integer id) {
		return dao.findById(id);
	}

	/**
	 * public Record findById(Integer id) { Record disasters = Db.findFirst("select
	 * id,dname,code,content,opentime from qx_disaster WHERE id = ? ORDER BY id
	 * DESC", id); return disasters; }
	 */

	/**
	 * 查找所有灾难信息
	 * 
	 * @return
	 */

	public List<QxDisaster> findDisasterList() {
		return dao.find("select id,dname,content,open_time from qx_disaster order by id desc");
	}

	public QxDisaster findModelById(Integer id) {
		return dao.findById(id);
	}

	/**
	 * 创建灾难
	 * 
	 * @param role
	 * @return
	 */
	public Ret addDisaster(QxDisaster disaster) {
		String name = disaster.getDname();
		if (StrKit.isBlank(name))
			return Ret.fail("msg", "灾难名称不能为空");
		if (StrKit.isBlank(disaster.getAddress()))
			return Ret.fail("msg", "请填写或者选择发生地点");
		Map<String, Object> map = MapUtils.java2Map(disaster);
		Boolean flag = disaster.save(map);
		if (flag)
			return Ret.ok("msg", "创建成功");
		return Ret.fail("msg", "创建失败");
	}

	/**
	 * 更新灾难信息
	 * 
	 * @param Depart
	 * @return
	 */
	public Ret upDisaster(QxDisaster disaster) {
		Boolean flag = disaster.update();
		if (flag)
			return Ret.ok("msg", "更新成功");
		return Ret.fail("msg", "更新失败");
	}

	/**
	 * 删除灾难
	 * 
	 * @param user
	 * @return
	 */
	public Ret delDisaster(QxDisaster disaster) {
		Boolean flag = disaster.delete();
		if (flag)
			return Ret.ok("msg", "删除成功");
		return Ret.fail("msg", "删除失败");
	}

	public List<String> getPaths(Integer count, List<String> data) {
		if (count > 0) {
			for (int i = 0; i < count; i++) {
				String path = "/viewImg/img/" + i + ".jpg";
				File file = new File("src/main/webapp" + path);
				if (!file.exists())
					continue;
				data.add(path);
			}
		}
		return data;
	}

	/**
	 * 获取对视频截取的图片列表
	 * 
	 * @return 2018年8月17日 上午11:22:04
	 */
	public List<String> getImgPaths() {
		File file = new File("src/main/webapp/viewImg/img");
		if (!file.exists()) {
			return null;
		}
		String[] content = file.list();
		List<String> list = new ArrayList<>();
		for (String name : content) {
			list.add("src/main/webapp/viewImg/img/" + name);
		}
		return list;
	}

	/**
	 * 获取基础信息中的所有带照片的人物信息
	 * 
	 * @return 2018年8月17日 上午11:29:39
	 */
	public List<Record> getUserlist() {
		return Db.find("select *,0 as flag from qx_user_library where picpath is not null");
	}

	/**
	 * 调用接口对人物照片信息进行对比
	 * 
	 * @param paths
	 * @param userList
	 * @return 2018年8月17日 上午11:36:45
	 */
	public List<Record> contrast(List<String> paths, List<Record> userList) {
		if (paths.size() == 0 || userList.size() == 0)
			return null;
		List<Record> data = new ArrayList<>();
		for (String path : paths) {
			for (Record userLibrary : userList) {
				Integer flag = userLibrary.getInt("FLAG");
				if (flag == 1)
					continue;
				String picPath = "src/main/webapp" + userLibrary.getStr("PICPATH");

				Ret ret = FaceMatch.match(path, picPath);
				if (ret.isOk()) {
					Integer score = ret.getInt("score");
					if (score > 50) {
						userLibrary.set("FLAG", 1);
						data.add(userLibrary);
					}
				}
			}
		}
		return data;
	}

	/**
	 * IBM图片对比比较
	 * 
	 * @param paths
	 * @param disId
	 * @return 2018年8月27日 下午6:28:17
	 */
	public List<Record> contrastByIbm(List<String> paths, Integer disId) {
		if (paths.size() == 0)
			return null;
		Set<String> set = new HashSet<String>();
		List<QxDisResult> reList = new ArrayList<>();
		for (String path : paths) {
			/*
			 * boolean flag = AnalytisPicUtil.faceAnalytis(path); if(!flag) continue;
			 */
			List<ClassScorePo> classList = AnalytisPicUtil.getClassScore(path);
			if (classList == null || classList.size() == 0)
				continue;
			String userCode = classList.get(0).getClassName();
			String score = classList.get(0).getScore();
			String resultFileName = FileUtil.instance.changepicName(path, ".jpg");
			QxDisResult po = new QxDisResult();
			po.setUsercode(userCode);
			po.setPicture("/viewImg/reimg/" + resultFileName);
			po.setScore(score);
			po.setDisid(disId);
			reList.add(po);
			set.add(userCode);
		}
		if (set.size() == 0)
			return null;
		List<Record> list = new ArrayList<>();
		for (String code : set) {
			Record record = Db.findFirst("select * from  qx_user_library where usercode = ? ", code);
			if (record == null)
				continue;
			Integer userId = record.getInt("ID");
			Record du = Db.findFirst("select * from qx_dis_user where userId = ? and disid = ? ", userId, disId);
			if (du != null)
				continue;
			QxDisUser disUser = new QxDisUser();
			disUser.setUserid(userId);
			disUser.setDisid(disId);
			Map<String, Object> map = MapUtils.java2Map(disUser);
			disUser.save(map);
			list.add(record);
		}
		if (reList.size() == 0)
			return list;
		for (QxDisResult disResult : reList) {
			Map<String, Object> map = MapUtils.java2Map(disResult);
			disResult.save(map);
		}
		return list;
	}

	public Ret editPicPath(String filePath, String fileName, String path) {
		String extension = fileName.substring(fileName.lastIndexOf("."));
		if (".mp4".equals(extension) || ".avi".equals(extension) || ".rmvb".equals(extension)
				|| ".rm".equals(extension)) {
			String resultFileName = FileUtil.instance.changeFileName(filePath, extension);// 为了避免相同文件上传冲突，使用工具类重新命名
			String src = path + resultFileName;
			Ret ret = Ret.ok("msg", src);
			return ret;
		} else {
			File source = new File(filePath);
			source.delete();
			Ret ret = Ret.fail("msg", "只允许上传mp4,rmvb,rm,avi类型的图片文件");
			return ret;
		}
	}

	public Page<Record> paginateResult(Integer paraToInt, Integer disId) {
		return Db.paginate(paraToInt, 20, "select ul.*,du.disid ",
				"from qx_user_library ul,qx_dis_user du where ul.id =du.userid and du.disid = ? ", disId);
	}

	/**
	 * 根据身份证号查询比对结果
	 * 
	 * @param code
	 * @param disId
	 * @return 2018年8月31日 上午8:42:14
	 */
	public List<Record> anaByUserCode(String code, Integer disId) {
		List<Record> list = Db.find("select * from qx_dis_result where usercode = ? and disid = ? order by score desc",
				code, disId);
		return list;
	}

	public Ret getImages(String[] paths) {
		Integer count = 0;
		for (String path : paths) {
			path = "src/main/webapp" + path;
			System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
			// 读取视频文件
			VideoCapture cap = new VideoCapture(path);
			// 判断视频是否打开
			if (cap.isOpened()) {
				// 总帧数
				double frameCount = cap.get(opencv_highgui.CV_CAP_PROP_FRAME_COUNT);
				// 帧率
				double fps = cap.get(opencv_highgui.CV_CAP_PROP_FPS);
				// 时间长度
				double len = frameCount / fps;
				Double d_s = new Double(len);
				Mat frame = new Mat();
				deleteDir("src/main/webapp/viewImg/img");
				for (int i = 0; i < d_s.intValue(); i++) {
					// 设置视频的位置(单位:毫秒)
					cap.set(opencv_highgui.CV_CAP_PROP_POS_MSEC, i * 3000);
					// 读取下一帧画面
					if (cap.read(frame)) {
						count++;
						// 保存画面到本地目录
						String fileName = "src/main/webapp/viewImg/img/" + count + ".jpg";
						Imgcodecs.imwrite(fileName, frame);
						Mat image = Imgcodecs.imread(new File(fileName).getAbsolutePath());
						// 初始化人脸识别类
						MatOfRect faceDetections = new MatOfRect();
						// 添加识别模型
						String xmlPath = VideoToImgUtils.class.getClassLoader()
								.getResource("haarcascade_frontalface_alt.xml").getPath().substring(1);
						CascadeClassifier faceDetector = new CascadeClassifier(xmlPath);
						// 识别脸
						faceDetector.detectMultiScale(image, faceDetections);
						if (faceDetections.toArray().length == 0) {
							File file = new File(fileName);
							file.delete();
						} else {
							// 画出脸的位置
							for (Rect rect : faceDetections.toArray()) {
								Imgproc.rectangle(image, new Point(rect.x, rect.y),
										new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 0, 255), 2);
							}
							Imgcodecs.imwrite(fileName, image);
						}
					}
				}
				// 关闭视频文件
				cap.release();
			}
		}
		if (count == 0)
			return Ret.fail("msg", "视频未读取到");
		return Ret.ok("msg", count);
	}

	/**
	 * 清空文件夹中的内容
	 * 
	 * @param path
	 * @return 2018年8月17日 上午10:46:05
	 */
	public static boolean deleteDir(String path) {
		File file = new File(path);
		if (!file.exists()) {// 判断是否待删除目录是否存在
			System.err.println("The dir are not exists!");
			return false;
		}

		String[] content = file.list();// 取得当前目录下所有文件和文件夹
		for (String name : content) {
			File temp = new File(path, name);
			if (temp.isDirectory()) {// 判断是否是目录
				deleteDir(temp.getAbsolutePath());// 递归调用，删除目录里的内容
			} else {
				if (!temp.delete()) {// 直接删除文件
					System.err.println("Failed to delete " + name);
				}
			}
		}
		return true;
	}

	public List<Record> statisSex(Integer disId) {
		List<Record> list = Db.find(
				"SELECT Q.name, Q.value FROM (select sum(case when ul.sex = '男' then 1 else 0 end) as 男性,sum(case when ul.sex = '女' then 1 else 0 end) as 女性,du.disid from qx_dis_user du,qx_user_library ul where du.userid = ul.id and du.disid = ? group by du.disid) AS S,TABLE (VALUES('男性', S.男性),('女性', S.女性)) AS Q(name, value)",
				disId);
		if (list.size() == 0)
			return null;
		List<Record> lowList = new ArrayList<>();
		for (Record record : list) {
			Record re = new Record();
			re.set("name", record.get("NAME"));
			re.set("value", record.get("VALUE"));
			lowList.add(re);
		}
		return lowList;
	}

	public Ret statisBlood(Integer disId) {
		List<Record> bloodList = Db.find(
				"SELECT Q.name, Q.value FROM (select sum(case when ul.blood = 'A' then 1 else 0 end) as A,sum(case when ul.blood = 'B' then 1 else 0 end) as B,sum(case when ul.blood = 'AB' then 1 else 0 end) as AB,sum(case when ul.blood = 'O' then 1 else 0 end) as O,du.disid from qx_dis_user du,qx_user_library ul where du.userid = ul.id and du.disid = ? group by du.disid) AS S,TABLE (VALUES('A', S.A),('B', S.B),('AB', S.AB),('O', S.O)) AS Q(name, value)",
				disId);
		if (bloodList.size() == 0)
			return Ret.fail("msg", "，没有数据");
		List<String> nameList = new ArrayList<>();
		List<Integer> valueList = new ArrayList<>();
		for (Record record : bloodList) {
			String name = record.get("NAME");
			Integer value = record.getInt("VALUE");
			nameList.add(name);
			valueList.add(value);
		}
		return Ret.ok().set("name", nameList).set("value", valueList);
	}

	/**
	 * 根据灾难地点获取视频列表
	 * 
	 * @param addr
	 * @return 2018年9月6日 下午5:18:39
	 */
	public Ret findVideo(String addr) {
		if (StrKit.isBlank(addr))
			return Ret.fail("msg", "未找到视频信息");
		List<QxCamarea> caList = caDao.find("select id,camname,address from qx_camarea where address = ? ", addr);
		if (caList == null || caList.size() == 0)
			return Ret.fail("msg", "未找到视频信息");
		Integer flag = 0;
		List<Record> list = new ArrayList<>();
		for (QxCamarea cam : caList) {
			List<Record> videoList = Db.find("select id,videopath from qx_video_library where camid = ? ", cam.getId());
			if (videoList == null || videoList.size() == 0)
				continue;
			Record record = new Record();
			record.set("CAMNAME", cam.getCamname());
			record.set("VIDEOS", videoList);
			list.add(record);
			flag++;
		}
		if (flag == 0)
			return Ret.fail("msg", "未找到视频信息");
		return Ret.ok("msg", list);
	}

	
}
