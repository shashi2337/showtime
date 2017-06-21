package com.thoughtworks.a70mm;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringBufferInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setDateSpinner();
        setLanguageSpinner();
        setGenereSpinner();
        setTimeSpinner();
    }

    private void setGenereSpinner() {
        Spinner genereSpinner = (Spinner) findViewById(R.id.genere_search);
        List<String> genereItems = new ArrayList<>();
        genereItems.add("Action");
        genereItems.add("Adventure");
        genereItems.add("Animation");
        genereItems.add("Comedy");
        genereItems.add("Crime");
        genereItems.add("Drama");
        genereItems.add("Family");
        genereItems.add("Horror");
        genereItems.add("Mystery");
        genereItems.add("Romance");
        genereItems.add("Sci-fi");
        genereItems.add("Telefilm");
        genereItems.add("Thriller");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, genereItems);
        genereSpinner.setAdapter(adapter);
    }

    private void setLanguageSpinner() {
        Spinner languageSpinner = (Spinner) findViewById(R.id.language_search);
        List<String> languages = new ArrayList<>();
        languages.add("All");
        languages.add("Hindi");
        languages.add("English");
        languages.add("Tamil");
        languages.add("Telugu");
        languages.add("Malyalam");
        languages.add("Kannada");
        languages.add("Marathi");
        languages.add("Bengali");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.sp, languages);
        languageSpinner.setAdapter(adapter);
    }

    private void setDateSpinner() {
        Spinner dateSpinner = (Spinner) findViewById(R.id.date_search);
        Calendar now = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("EEE, MMM d");
        String[] days = new String[7];
        for (int i = 0; i < 7; i++)
        {
            days[i] = format.format(now.getTime());
            now.add(Calendar.DAY_OF_MONTH, 1);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, days);
        dateSpinner.setAdapter(adapter);
    }

    private void setTimeSpinner() {
        Spinner timeSpinner = (Spinner) findViewById(R.id.movie_time_search);
        List<String> spinnerItems = new ArrayList<>();
        spinnerItems.add("Before 8 AM");
        spinnerItems.add("8 AM - 12 PM");
        spinnerItems.add("12 PM -8 PM");
        spinnerItems.add("After 8 PM");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerItems);
        timeSpinner.setAdapter(adapter);
        Calendar calendar = new GregorianCalendar();
        int selectedPosition = 0;
        int currentHour = calendar.getTime().getHours();
        if (currentHour >= 0 && currentHour <= 8) {
            selectedPosition = 0;
        } else if (currentHour >= 8 && currentHour <= 12) {
            selectedPosition = 1;
        } else if (currentHour >= 12 && currentHour <= 20) {
            selectedPosition = 2;
        } else if (currentHour >= 20 && currentHour <= 24) {
            selectedPosition = 3;
        }
        timeSpinner.setSelection(selectedPosition);
    }

    private void setUpMovieList(List<Movie> movies) {
        ListView moviesList = (ListView) findViewById(R.id.movie_list);
        MoviesAdapter moviesAdapter = new MoviesAdapter(this, movies);
        moviesList.setAdapter(moviesAdapter);
    }

    public void onSearch(View view) throws ParseException {
        final Calendar selectedDate = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d");
        selectedDate.setTime(sdf.parse((String) ((Spinner) findViewById(R.id.date_search)).getSelectedItem()));
        final String selectedLanguage = (String) ((Spinner) findViewById(R.id.language_search)).getSelectedItem();
        final String selectedGenere = (String) ((Spinner) findViewById(R.id.genere_search)).getSelectedItem();
        final String selectedTime = (String) ((Spinner) findViewById(R.id.movie_time_search)).getSelectedItem();

        new AsyncTask<Object, Object, List<Movie>>() {
            @Override
            protected List<Movie> doInBackground(Object... params) {
                List<Movie> movieList = new ArrayList<Movie>();
                String startTime = null;
                String endTime = null;
                String datePrefix = String.valueOf(Calendar.getInstance().get(Calendar.YEAR)) +
                        String.valueOf(String.format("%02d", selectedDate.get(Calendar.MONTH) + 1)) +
                        String.valueOf(String.format("%02d", selectedDate.get(Calendar.DAY_OF_MONTH)));
                if ("Before 8 AM".equals(selectedTime)) {
                    startTime = datePrefix + "0000";
                    endTime = datePrefix + "0759";
                } else if ("8 AM - 12 PM".equals(selectedTime)) {
                    startTime = datePrefix + "0800";
                    endTime = datePrefix + "1159";
                } else if ("12 PM -8 PM".equals(selectedTime)) {
                    startTime = datePrefix + "1200";
                    endTime = datePrefix + "1959";
                } else if ("After 8 PM".equals(selectedTime)) {
                    startTime = datePrefix + "2000";
                    endTime = datePrefix + "2359";
                }
                String API_URL = "http://timesofindia.indiatimes.com/tvmoviedata1.cms?";
                try {
                    URL url = new URL(API_URL +
                            "fromdatetime=" + startTime +
                            "&todatetime=" + endTime +
                            "&userid=" + 0 +
                            "&pageno=" + 1 +
                            "&languagelist=" + selectedLanguage +
                            "&subgenrename=" + selectedGenere +
                            "&channelname=" +
                            "&noofrecords" + 24);

                    HttpURLConnection urlConnection = null;
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    BufferedReader bufferedReader = null;
                    bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();

                    movieList = parseMovieList(stringBuilder);
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (SAXException e) {
                    e.printStackTrace();
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return movieList;
            }

            @Override
            protected void onPostExecute(List<Movie> movieList) {
                super.onPostExecute(movieList);
                setUpMovieList(movieList);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private List<Movie> parseMovieList(StringBuilder stringBuilder) throws ParserConfigurationException, SAXException, IOException {
        List<Movie> movies = new ArrayList<>();
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        stringBuilder.insert(0, "<?xml version=\"1.0\"?> <figures>");
        stringBuilder.append("</figures>");
        Document doc = dBuilder.parse(new StringBufferInputStream(stringBuilder.toString().replaceAll("&nbsp;", "")));
        if (doc.getChildNodes() != null && doc.getChildNodes().getLength() == 1) {
            NodeList figureNodeList = doc.getFirstChild().getChildNodes();
            if (figureNodeList != null) {
                for (int index = 0; index < figureNodeList.getLength(); index++) {
                    Node figureNode = figureNodeList.item(index);
                    if (figureNode == null) continue;
                    Node divNode = figureNode.getFirstChild();
                    if (divNode == null) continue;
                    Node aNode = divNode.getLastChild();
                    if (aNode == null) continue;
                    Node figCaptionNode = aNode.getLastChild();
                    if (figCaptionNode == null) continue;
                    Node spaninfoNode = figCaptionNode.getFirstChild();
                    if (spaninfoNode == null) continue;
                    String movieName = spaninfoNode.getChildNodes().item(0).getChildNodes().item(0).getNodeValue();
                    String movieDescription = spaninfoNode.getChildNodes().item(1).getChildNodes().item(0).getNodeValue();
                    String movieSchedule = spaninfoNode.getChildNodes().item(2).getChildNodes().item(0).getNodeValue();
                    movies.add(new Movie(movieName, movieDescription, movieSchedule));
                }
            }
        }
        return movies;
    }

}