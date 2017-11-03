package ph.edu.dlsu.controller;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

@RestController
public class Controller {

	// Save the uploaded file to this folder
	private static String UPLOADED_FOLDER = "C:\\Users\\Specter94\\Desktop\\UI-Display-Tool-master\\uploaded\\";

	@RequestMapping(value = { "/loadJSON" }, method = RequestMethod.GET)
	public ModelAndView loadJSON() {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("loadJSON");
		return modelAndView;
	}

	@PostMapping("/upload") // //new annotation since 4.3
	public ModelAndView singleFileUpload(@RequestParam("file") MultipartFile file,
			RedirectAttributes redirectAttributes) {

		if (file.isEmpty()) {
			redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
		}

		// Get the file and save it somewhere
		byte[] bytes = null;
		try {
			bytes = file.getBytes();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
		try {
			Files.write(path, bytes);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// JSON parser object to parse read file
		JSONParser jsonParser = new JSONParser();

		try (FileReader reader = new FileReader(UPLOADED_FOLDER + file.getOriginalFilename())) {
			// Read JSON file
			Object obj = jsonParser.parse(reader);

			JSONArray uiList = (JSONArray) obj;
			System.out.println(uiList);
			System.out.println();

			JSONObject labelsList = null;
			JSONObject buttonsList = null;
			JSONObject textFieldsList = null;

			for (int i = 0; i < uiList.size(); i++) {
				JSONObject object = (JSONObject) uiList.get(i);
				String check = object + "";
				if (check.contains("Labels")) {
					labelsList = object;
				} else if (check.contains("Buttons")) {
					buttonsList = object;
				} else if (check.contains("Text Fields")) {
					textFieldsList = object;
				}
			}

			loadUI(labelsList, buttonsList, textFieldsList);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		try {
			TimeUnit.SECONDS.sleep(5);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("renderedUI");
		return modelAndView;
	}

	public static void loadUI(JSONObject labelsList, JSONObject buttonsList, JSONObject textFieldsList) {

		File file = new File(
				"C:\\Users\\Specter94\\Desktop\\UI-Display-Tool-master\\src\\main\\resources\\templates\\renderedUI.html");
		String htmlPage = "<!DOCTYPE html>\r\n" + "<html xmlns=\"http://www.w3.org/1999/xhtml\"\r\n"
				+ "	xmlns:th=\"http://www.thymeleaf.org\">\r\n" + "\r\n" + "<head>\r\n"
				+ "<title>renderedUI</title>\r\n" + "<link rel=\"stylesheet\"\r\n"
				+ "	href=\"http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css\" />\r\n"
				+ "<script\r\n"
				+ "	src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js\"></script>\r\n"
				+ "<script\r\n"
				+ "	src=\"http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js\"></script>\r\n"
				+ "</head>\r\n" + "\r\n" + "<body class=\"body\" style=\"background-color: #f6f6f6\">";

		FileWriter fileWriter = null;
		BufferedWriter bufferedWriter = null;
		try {
			fileWriter = new FileWriter(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		bufferedWriter = new BufferedWriter(fileWriter);

		try {
			bufferedWriter.write(htmlPage);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (labelsList != null) {
			JSONArray lList = (JSONArray) labelsList.get("Labels");
			for (int i = 0; i < lList.size(); i++) {
				JSONObject obj = (JSONObject) lList.get(i);
				System.out.println(obj);

				String positionX = obj.get("PositionX").toString();
				String width = obj.get("Width").toString();

				float adjustedWidth = Float.parseFloat(width) + 15;
				float adjustedX = Float.parseFloat(positionX) - 15;
				System.out.println(adjustedWidth);
				System.out.println(adjustedX);

				try {
					bufferedWriter.append("<label style=\"position:absolute;" + "left:" + adjustedX + "px;"
							+ "top:" + obj.get("PositionY") + "px;" + "width:" + adjustedWidth + "px;" + "height:"
							+ obj.get("Height") + "px;" + "display:inline-block;\">" + obj.get("Text") + "</label>");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		if (buttonsList != null) {

			JSONArray bList = (JSONArray) buttonsList.get("Buttons");
			for (int i = 0; i < bList.size(); i++) {
				JSONObject obj = (JSONObject) bList.get(i);
				System.out.println(obj);

				String width = obj.get("Width").toString();

				float adjustedWidth = Float.parseFloat(width) + 15;
				System.out.println(adjustedWidth);

				try {
					bufferedWriter.append("<button type=\"button\" style=\"position:absolute;" + "left:"
							+ obj.get("PositionX") + "px;" + "top:" + obj.get("PositionY") + "px;" + "width:"
							+ adjustedWidth + "px;" + "height:" + obj.get("Height") + "px;"
							+ "display:inline-block;\">" + obj.get("Text") + "</button>");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		if (textFieldsList != null) {
			JSONArray tList = (JSONArray) textFieldsList.get("Text Fields");
			for (int i = 0; i < tList.size(); i++) {
				JSONObject obj = (JSONObject) tList.get(i);
				System.out.println(obj);
				try {
					bufferedWriter.append("<input type=\"text\" value=\"" + obj.get("Text")
							+ "\"style=\"position:absolute;" + "left:" + obj.get("PositionX") + "px;" + "top:"
							+ obj.get("PositionY") + "px;" + "width:" + obj.get("Width") + "px;" + "height:"
							+ obj.get("Height") + "px;" + "display:inline-block;\"></input>");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		// Ending..
		try {
			bufferedWriter.append("</body>\r\n" + "\r\n" + "</html>");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Html page created");
		try {
			bufferedWriter.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			fileWriter.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			bufferedWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			fileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
