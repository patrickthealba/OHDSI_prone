# NOTE: before running this script be sure to set the 
# working directory to the location of this file

bashstring <- "sudo docker build -t jdposa/prone_nlp:0.1 ."
system(bashstring)

# bash_string = """
# sudo docker push jdposa/prone_nlp:0.1
# """
# os.system(bash_string)